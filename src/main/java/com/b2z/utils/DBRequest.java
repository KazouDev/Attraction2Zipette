package com.b2z.utils;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DBRequest {

    public static <T> List<T> execute(
            @NotNull  String query,
            @Nullable Function<ResultSet, T> callback
    ) {
        return DBRequest.execute(query, null, callback);
    }

    public static <T> List<T> execute(
            @NotNull  String query
    ) {
        return DBRequest.execute(query, null, null);
    }

    public static <T> List<T> execute(
            @NotNull  String query,
            @Nullable Map<Integer, Object> params,
            @Nullable Function<ResultSet, T> callback
    ) {
        try {
            return DBRequest.request(query, params, callback);
        } catch(SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static boolean execute(
            @NotNull String query,
            @Nullable Map<Integer, Object> params
    ) {
        try {
            DBRequest.request(query, params, null);
            return true;
        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static <T> List<T> request(
            @NotNull  String query,
            @Nullable Map<Integer, Object> params,
            @Nullable Function<ResultSet, T> callback
    ) throws SQLException {
        final List<T> results = new ArrayList<>();

        Connection conn;
        PreparedStatement stmt;
        ResultSet rs = null;

        boolean isInsert = query.trim().toUpperCase().contains("INSERT");
        conn = DBManager.getInstance().getConnection();

        if (isInsert) {
            stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        } else {
            stmt = conn.prepareStatement(query);
        }

        if(params != null) {
            for(Map.Entry<Integer, Object> entry : params.entrySet()) {
                stmt.setObject(entry.getKey(), entry.getValue());
            }
        }

        if (query.trim().toUpperCase().contains("SELECT")) {
            rs = stmt.executeQuery();
            if (callback != null) {
                while (rs.next()) {
                    T result = callback.apply(rs);
                    if (result != null) {
                        results.add(result);
                    }
                }

                DBManager.getInstance().cleanup(conn, stmt, rs);
                return results;
            }
        } else if (isInsert) {
            stmt.executeUpdate();
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1);
                String tableName = extractTableNameFromInsert(query);

                if (tableName != null) {
                    String selectQuery = "SELECT * FROM " + tableName + " WHERE id = ?";
                    List<T> selectResults = execute(selectQuery, Map.of(1, id), callback);
                    DBManager.getInstance().cleanup(conn, stmt, generatedKeys);
                    return selectResults;
                } else {
                    DBManager.getInstance().cleanup(conn, stmt, generatedKeys);
                    return null;
                }
            }
        } else {
            stmt.executeUpdate();
            DBManager.getInstance().cleanup(conn, stmt, rs);
            return null;
        }
        return null;
    }

    private static String extractTableNameFromInsert(String query) {
        String upper = query.toUpperCase();
        int idx = upper.indexOf("INSERT INTO");
        if (idx == -1) return null;
        String afterInsert = query.substring(idx + "INSERT INTO".length()).trim();
        String[] parts = afterInsert.split("\\s+|\\(");
        return parts.length > 0 ? parts[0] : null;
    }

}
