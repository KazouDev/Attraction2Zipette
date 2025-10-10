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
        ResultSet rs;

        conn = DBManager.getInstance().getConnection();
        stmt = conn.prepareStatement(query);

        if(params != null) {
            for(Map.Entry<Integer, Object> entry : params.entrySet()) {
                stmt.setObject(entry.getKey(), entry.getValue());
            }
        }

        rs = stmt.executeQuery();

        if(callback != null) {
            while (rs.next()) {
                T result = callback.apply(rs);
                if(result != null) {
                    results.add(result);
                }
            }
            return results;
        }
        DBManager.getInstance().cleanup(conn, stmt, rs);
        return null;
    }

}
