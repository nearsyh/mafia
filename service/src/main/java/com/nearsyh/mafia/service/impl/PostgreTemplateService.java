package com.nearsyh.mafia.service.impl;

import static com.nearsyh.mafia.common.postgres.QueryUtils.constructDeleteByColumnsSql;
import static com.nearsyh.mafia.common.postgres.QueryUtils.constructGetByColumnsSql;

import com.google.protobuf.InvalidProtocolBufferException;
import com.nearsyh.mafia.common.postgres.QueryUtils;
import com.nearsyh.mafia.protos.Template;
import com.nearsyh.mafia.service.TemplateService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class PostgreTemplateService implements TemplateService {

    private static final String TEMPLATES_TABLE = "Templates";
    private static final String TEMPLATES_PLAYERS_NUMBER_FIELD = "PlayersNumber";
    private static final String TEMPLATES_NAME_FIELD = "Name";
    private static final String TEMPLATES_BINARY_FIELD = "TemplateBinary";
    private static final String TEMPLATES_ACCESS_TIMES_FIELD = "AccessTimes";

    private final DataSource dataSource;

    public PostgreTemplateService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Mono<Template> getTemplate(int playersNumber, String name) {
        return QueryUtils.doWorkReactiveWithReactiveFunction(dataSource, connection -> {
            try (var statement = connection.prepareStatement(
                constructGetByColumnsSql(
                    TEMPLATES_TABLE,
                    List.of(TEMPLATES_PLAYERS_NUMBER_FIELD, TEMPLATES_NAME_FIELD)))) {
                statement.setInt(1, playersNumber);
                statement.setString(2, name);
                var resultSet = statement.executeQuery();
                var template = resultSet.next()
                    ? Template.parseFrom(resultSet.getBytes(TEMPLATES_BINARY_FIELD))
                    : null;
                if (template != null) {
                    template = template.toBuilder()
                        .setAccessTimes(template.getAccessTimes() + 1).build();
                    return upsertTemplate(playersNumber, name, template);
                }
                return Mono.justOrEmpty(template);
            } catch (InvalidProtocolBufferException e) {
                return Mono.empty();
            }
        });
    }

    @Override
    public Mono<Template> upsertTemplate(int playersNumber, String name,
        Map<String, Integer> counts) {
        return upsertTemplate(playersNumber, name, Template.newBuilder()
            .setPlayersNumber(playersNumber)
            .putAllCounts(counts)
            .setName(name)
            .build());
    }

    private Mono<Template> upsertTemplate(int playersNumber, String name, Template template) {
        return QueryUtils.doWorkReactive(dataSource, connection -> {
            try (var statement = connection.prepareStatement(
                String.format(
                    "INSERT INTO %s (%s, %s, %s, %s) VALUES (?,?,?,?) "
                        + "ON CONFLICT (%s, %s) DO UPDATE "
                        + "SET %s=?,%s=?",
                    TEMPLATES_TABLE,
                    TEMPLATES_PLAYERS_NUMBER_FIELD, TEMPLATES_NAME_FIELD, TEMPLATES_BINARY_FIELD,
                    TEMPLATES_ACCESS_TIMES_FIELD,
                    TEMPLATES_PLAYERS_NUMBER_FIELD, TEMPLATES_NAME_FIELD, TEMPLATES_BINARY_FIELD,
                    TEMPLATES_ACCESS_TIMES_FIELD))) {
                statement.setInt(1, playersNumber);
                statement.setString(2, name);
                statement.setBytes(3, template.toByteArray());
                statement.setInt(4, template.getAccessTimes());
                statement.setInt(5, playersNumber);
                statement.setString(6, name);
                statement.setBytes(7, template.toByteArray());
                statement.setInt(8, template.getAccessTimes());
                statement.execute();
                return template;
            }
        });
    }

    @Override
    public Mono<List<String>> listTemplates(int playersNumber) {
        return QueryUtils.doWorkReactive(dataSource, connection -> {
            try (var statement = connection.prepareStatement(String.format(
                "SELECT %s FROM %s WHERE %s = '%s' ORDER BY %s",
                TEMPLATES_NAME_FIELD, TEMPLATES_TABLE,
                TEMPLATES_PLAYERS_NUMBER_FIELD, playersNumber,
                TEMPLATES_ACCESS_TIMES_FIELD))) {
                var list = new ArrayList<String>();
                var resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    list.add(resultSet.getString(TEMPLATES_NAME_FIELD));
                }
                return list;
            }
        });
    }

    @Override
    public Mono<Template> deleteTemplate(int playersNumber, String name) {
        return QueryUtils.doWorkReactive(dataSource, connection -> {
            try (var statement = connection.prepareStatement(
                constructDeleteByColumnsSql(
                    TEMPLATES_TABLE,
                    List.of(TEMPLATES_PLAYERS_NUMBER_FIELD, TEMPLATES_NAME_FIELD)))) {
                statement.setInt(1, playersNumber);
                statement.setString(2, name);
                statement.execute();
                return null;
            }
        });
    }
}
