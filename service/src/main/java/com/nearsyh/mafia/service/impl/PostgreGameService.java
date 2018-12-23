package com.nearsyh.mafia.service.impl;

import static com.nearsyh.mafia.common.postgres.QueryUtils.constructGetByColumnsSql;
import static com.nearsyh.mafia.common.postgres.QueryUtils.constructInsertSql;
import static com.nearsyh.mafia.common.postgres.QueryUtils.constructUpdateSql;

import com.google.protobuf.InvalidProtocolBufferException;
import com.nearsyh.mafia.common.postgres.QueryUtils;
import com.nearsyh.mafia.protos.Game;
import com.nearsyh.mafia.service.GameService;
import java.util.LinkedHashMap;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class PostgreGameService implements GameService {

    private static final String GAME_TABLE = "Games";
    private static final String GAME_ID_FIELD = "GameId";
    private static final String GAME_BINARY_FIELD = "GameBinary";
    private static final String GAME_START_TIMESTAMP_FIELD = "StartTimestamp";

    private final DataSource dataSource;

    public PostgreGameService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Mono<Game> getGame(String gameId) {
        return QueryUtils.doWorkReactive(dataSource, connection -> {
            try (var statement = connection.prepareStatement(
                constructGetByColumnsSql(GAME_TABLE, List.of(GAME_ID_FIELD)))) {
                statement.setString(1, gameId);
                var resultSet = statement.executeQuery();
                return resultSet.next()
                    ? Game.parseFrom(resultSet.getBytes(GAME_BINARY_FIELD))
                    : null;
            } catch (InvalidProtocolBufferException e) {
                return null;
            }
        });
    }

    @Override
    public Mono<Game> createGame(Game game) {
        return QueryUtils.doWorkReactive(dataSource, connection -> {
            try (var statement = connection.prepareStatement(
                constructInsertSql(GAME_TABLE, List.of(GAME_ID_FIELD),
                    GAME_BINARY_FIELD, GAME_START_TIMESTAMP_FIELD))) {
                statement.setString(1, game.getId());
                statement.setBytes(2, game.toByteArray());
                statement.setLong(3, game.getStartTimestamp());
                statement.execute();
                return game;
            }
        });
    }

    @Override
    public Mono<Game> updateGame(Game game) {
        return QueryUtils.doWorkReactive(dataSource, connection -> {
            try (var statement = connection.prepareStatement(
                constructUpdateSql(GAME_TABLE, List.of(GAME_ID_FIELD),
                    GAME_BINARY_FIELD, GAME_START_TIMESTAMP_FIELD))) {
                statement.setBytes(1, game.toByteArray());
                statement.setLong(2, game.getStartTimestamp());
                statement.setString(3, game.getId());
                statement.execute();
                return game;
            }
        });
    }

    @Override
    public Mono<LinkedHashMap<String, Long>> getRecentGames(int count) {
        return QueryUtils.doWorkReactive(dataSource, connection -> {
            try (var statement = connection.prepareStatement(
                String.format("SELECT * FROM %s ORDER BY %s DESC LIMIT %s",
                    GAME_TABLE, GAME_START_TIMESTAMP_FIELD, count))) {
                var resultSet = statement.executeQuery();
                var ret = new LinkedHashMap<String, Long>();
                while (resultSet.next()) {
                    ret.put(resultSet.getString(GAME_ID_FIELD),
                        resultSet.getLong(GAME_START_TIMESTAMP_FIELD));
                }
                return ret;
            }
        });
    }
}
