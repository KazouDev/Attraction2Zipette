package com.b2z.utils;

import com.b2z.dao.IdResponse;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.sql.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DBRequest {

    public static <T> List<T> executeSelect(
            @NotNull String query,
            @Nullable Function<ResultSet, T> callback
    ) {
        return executeSelect(query, null, callback);
    }

    public static <T> List<T> executeSelect(
            @NotNull String query,
            @Nullable Map<Integer, Object> params,
            @Nullable Function<ResultSet, T> callback
    ) {
        try {
            String upperQuery = query.trim().toUpperCase();
            if (upperQuery.startsWith("SELECT")) {
                if (callback == null) {
                    throw new IllegalArgumentException("Callback required for SELECT queries");
                }
                System.out.println("call sub select " + ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
                List<T> l = select(query, params, callback);
                System.out.println("end call sub select at " + ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));

                return l;
            }
            return new ArrayList<>();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static IdResponse executeCommand(
            @NotNull String query,
            @Nullable Map<Integer, Object> params
    ) {
        try {
            return command(query, params);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static <T> List<T> select(
            @NotNull String query,
            @Nullable Map<Integer, Object> params,
            @NotNull Function<ResultSet, T> callback
    ) throws SQLException {
        List<T> results = new ArrayList<>();
        // Mesurer le temps d'acquisition de la connexion
        long t0 = System.nanoTime();
        Connection conn = DBManager.getInstance().getConnection();
        long t1 = System.nanoTime();
        long acquireMs = (t1 - t0) / 1_000_000;
        System.out.println("Connection acquired at " + ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")) + " (acquireMs=" + acquireMs + "ms)");

        if (conn == null) {
            throw new SQLException("Unable to obtain DB connection");
        }

        // Préparer la requête et mesurer le temps de préparation
        long p0 = System.nanoTime();
        PreparedStatement stmt = conn.prepareStatement(query);
        long p1 = System.nanoTime();
        long prepareMs = (p1 - p0) / 1_000_000;
        System.out.println("Prepared statement in " + prepareMs + "ms at " + ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));

        ResultSet rs = null;

        try {
            if (params != null) {
                for (Map.Entry<Integer, Object> entry : params.entrySet()) {
                    stmt.setObject(entry.getKey(), entry.getValue());
                }
            }

            long e0 = System.nanoTime();
            System.out.println("Executing query (" + query + ")at time : " + ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
            rs = stmt.executeQuery();
            long e1 = System.nanoTime();
            long execMs = (e1 - e0) / 1_000_000;
            System.out.println("Query (" + query + "executed in " + execMs + "ms at " + ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
            long totalMs = (e1 - t0) / 1_000_000;
            System.out.println("Total select time (acquire+prepare+execute) = " + totalMs + "ms");
            while (rs.next()) {
                T result = callback.apply(rs);
                if (result != null) {
                    results.add(result);
                }
            }
            return results;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static IdResponse command(
            @NotNull String query,
            @Nullable Map<Integer, Object> params
    ) throws SQLException {
        Connection conn = DBManager.getInstance().getConnection();
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;

        try {
            boolean isInsert = query.trim().toUpperCase().startsWith("INSERT");

            if (isInsert) {
                stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            } else {
                stmt = conn.prepareStatement(query);
            }

            if (params != null) {
                for (Map.Entry<Integer, Object> entry : params.entrySet()) {
                    stmt.setObject(entry.getKey(), entry.getValue());
                }
            }

            int affectedRows = stmt.executeUpdate();

            if (isInsert) {
                generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return new IdResponse(generatedKeys.getInt(1));
                }
            } else if (params != null) {
                if (affectedRows == 0) {
                    return null;
                }
                Object lastParam = params.get(params.size());
                if (lastParam instanceof Integer) {
                    return new IdResponse((Integer) lastParam);
                }
            }

            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}