package com.nearsyh.mafia.common.postgres;

import com.google.common.base.Preconditions;
import com.nearsyh.mafia.common.fn.CheckedFunction;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.sql.DataSource;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

public final class QueryUtils {

    private static final String CLAUSE_DELIMITER = ", ";
    private static final String QUESTION_MARK = "?";
    private static final String SET_VALUE_CLAUSE_FORMAT = "%s=?";
    private static final String WHERE_VALUE_CLAUSE_FORMAT = "%s=?";
    private static final String AND = " AND ";

    private QueryUtils() {
    }

    public static <U> Mono<U> doWorkReactive(
        DataSource dataSource, CheckedFunction<Connection, U, SQLException> work) {
        try (Connection connection = dataSource.getConnection()) {
            return Mono.justOrEmpty(work.apply(connection));
        } catch (SQLException e) {
            return Mono.error(e);
        }
    }

    public static <U> Mono<U> doWorkReactiveWithReactiveFunction(
        DataSource dataSource, CheckedFunction<Connection, Mono<U>, SQLException> work) {
        try (Connection connection = dataSource.getConnection()) {
            return work.apply(connection);
        } catch (SQLException e) {
            return Mono.error(e);
        }
    }

    public static <U> U doWork(DataSource dataSource,
        CheckedFunction<Connection, U, SQLException> work) {
        try (Connection connection = dataSource.getConnection()) {
            return work.apply(connection);
        } catch (SQLException e) {
            throw Exceptions.bubble(e);
        }
    }

    public static String constructInsertSql(String tableName, List<String> primaryKeys,
        String... otherColumns) {
        Preconditions.checkArgument(otherColumns.length > 0);
        var joinedPkColumns = String.join(CLAUSE_DELIMITER, primaryKeys);
        var joinedOtherColumns = String.join(CLAUSE_DELIMITER, otherColumns);
        var joinedColumnNames = String.join(CLAUSE_DELIMITER, joinedPkColumns, joinedOtherColumns);
        var questionMarks = IntStream
            .range(0, primaryKeys.size() + otherColumns.length)
            .mapToObj(any -> QUESTION_MARK)
            .collect(Collectors.joining(CLAUSE_DELIMITER));
        return String.format(
            "INSERT INTO %s (%s) VALUES (%s)", tableName, joinedColumnNames, questionMarks);
    }

    public static String constructUpdateSql(String tableName, List<String> primaryKeys,
        String... otherColumns) {
        Preconditions.checkArgument(otherColumns.length > 0);
        var setValueClauses = Stream.of(otherColumns)
            .map(column -> String.format(SET_VALUE_CLAUSE_FORMAT, column))
            .collect(Collectors.joining(CLAUSE_DELIMITER));
        return String.format(
            "UPDATE %s SET %s WHERE %s",
            tableName, setValueClauses, constructWhereClauses(primaryKeys));
    }

    public static String constructGetByColumnsSql(String tableName, List<String> columns) {
        var whereClauses = columns.stream()
            .map(column -> String.format(WHERE_VALUE_CLAUSE_FORMAT, column))
            .collect(Collectors.joining(AND));
        return String.format(
            "SELECT * FROM %s WHERE %s",
            tableName, constructWhereClauses(columns));
    }

    public static String constructDeleteByColumnsSql(String tableName, List<String> columns) {
        return String.format(
            "DELETE FROM %s WHERE %s",
            tableName, constructWhereClauses(columns));
    }

    private static String constructWhereClauses(List<String> columns) {
        return columns.stream()
            .map(column -> String.format(WHERE_VALUE_CLAUSE_FORMAT, column))
            .collect(Collectors.joining(AND));
    }
}
