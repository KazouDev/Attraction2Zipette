package com.b2z.dao;

import com.b2z.model.Lieu;
import com.b2z.service.ThemeParkAPI;
import com.b2z.utils.DBRequest;
import com.b2z.utils.Utils;

import java.util.List;
import java.util.Map;

public class LieuDAO implements DAOInterface<Lieu, LieuDAO.LieuProps> {

    public record LieuProps(
            String nom
    ) {}

    @Override
    public List<Lieu> findAll() {
        final String QUERY_STRING = """
            SELECT %s
            FROM Lieu %s
        """.formatted(Lieu.SELECT_COLUMNS, Lieu.ALIAS_NAME);
        return DBRequest.executeSelect(QUERY_STRING, null, Lieu::fromResultSet);
    }

    @Override
    public Lieu findById(int id) {
        final String QUERY_STRING = """
            SELECT %s
            FROM Lieu %s
        """.formatted(Lieu.SELECT_COLUMNS, Lieu.ALIAS_NAME);

        List<Lieu> result = DBRequest.executeSelect(QUERY_STRING, null, Lieu::fromResultSet);
        if (result == null || result.isEmpty()) {
            throw new IllegalArgumentException("Lieu introuvable");
        }

        return result.get(0);
    }

    @Override
    public IdResponse create(LieuProps props) {
        final String QUERY_STRING = """
            INSERT INTO Lieu (nom)
            VALUES (?)
        """;
        return DBRequest.executeCommand(QUERY_STRING, Utils.map(1, props.nom()));

    }

    @Override
    public IdResponse delete(int id) {
        final String QUERY_CHECK = """
            SELECT COUNT(*) AS count
            FROM Spectacle
            WHERE lieu_id = ?
        """;
        List<Integer> checkResult = DBRequest.executeSelect(QUERY_CHECK, Utils.map(1, id), rs -> {
            try {
                return rs.getInt("count");
            } catch (java.sql.SQLException e) {
                return null;
            }
        });

        if (checkResult != null && !checkResult.isEmpty()) {
            int count = checkResult.get(0);
            if (count > 0) {
                throw new IllegalArgumentException("Impossible de supprimer ce lieu car il est référencé par des spectacles.");
            }
        }

        final String QUERY_STRING = """
            DELETE FROM Lieu
            WHERE id = ?
        """;
        IdResponse result = DBRequest.executeCommand(QUERY_STRING, Utils.map(1, id));
        if (result == null) {
            throw new IllegalArgumentException("Lieu introuvable");
        } else {
            ThemeParkAPI.getInstance().removeAttraction(Integer.toString(id));
            return result;
        }
    }

    public IdResponse update(int id, Map<String, Object> updates) {
        if (updates == null || updates.isEmpty()) {
            return new IdResponse(id);
        }
        StringBuilder sql = new StringBuilder("UPDATE Lieu SET ");
        Map<Integer, Object> params = new java.util.HashMap<>();
        int paramIndex = 1;

        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            sql.append(entry.getKey()).append(" = ?, ");
            params.put(paramIndex++, entry.getValue());
        }

        sql.setLength(sql.length() - 2);
        sql.append(" WHERE id = ?");
        params.put(paramIndex, id);

        return DBRequest.executeCommand(sql.toString(), params);
    }


}
