package com.b2z.utils;

import com.b2z.dao.IdResponse;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.sql.*;
import java.util.ArrayList;
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
                return select(query, params, callback);
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
        Connection conn = DBManager.getInstance().getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        ResultSet rs = null;

        try {
            if (params != null) {
                for (Map.Entry<Integer, Object> entry : params.entrySet()) {
                    stmt.setObject(entry.getKey(), entry.getValue());
                }
            }

            rs = stmt.executeQuery();
            while (rs.next()) {
                T result = callback.apply(rs);
                if (result != null) {
                    results.add(result);
                }
            }
            return results;
        } finally {
            DBManager.getInstance().cleanup(conn, stmt, rs);
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
        } finally {
            DBManager.getInstance().cleanup(conn, stmt, generatedKeys);
        }
    }
}