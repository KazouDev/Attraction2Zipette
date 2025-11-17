package com.b2z.dao;

import com.b2z.model.AttractionType;
import com.b2z.utils.DBRequest;
import com.b2z.utils.Utils;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Map;

public class AttractionTypeDAO implements DAOInterface<AttractionType, AttractionTypeDAO.AttractionTypeProps> {

    public record AttractionTypeProps(
            @NotNull @Size(min = 3, max = 30) String nom
    ) {}

    @Override
    public List<AttractionType> findAll() {
        final String QUERY_STRING = """
            SELECT %s
            FROM AttractionType %s
    """.formatted(AttractionType.SELECT_COLUMNS, AttractionType.ALIAS_NAME);
        return DBRequest.executeSelect(QUERY_STRING, null, AttractionType::fromResultSet);
    }

    @Override
    public AttractionType findById(int id) {
        final String QUERY_STRING = """
            SELECT %s
            FROM AttractionType %s
            WHERE %s.id = ?
    """.formatted(AttractionType.SELECT_COLUMNS, AttractionType.ALIAS_NAME, AttractionType.ALIAS_NAME);

        List<AttractionType> result = DBRequest.executeSelect(QUERY_STRING, Utils.map(1, id), AttractionType::fromResultSet);
        if (result == null || result.isEmpty()) {
            throw new IllegalArgumentException("Lieu introuvable");
        }

        return result.get(0);
    }

    @Override
    public IdResponse create(AttractionTypeProps props) {
        final String QUERY_STRING = """
            INSERT INTO AttractionType (nom)
            VALUES (?)
        """;
        return DBRequest.executeCommand(QUERY_STRING, Utils.map(1, props.nom()));

    }

    @Override
    public IdResponse delete(int id) {
        final String QUERY_STRING = """
            DELETE FROM AttractionType
            WHERE id = ?
        """;
        IdResponse result = DBRequest.executeCommand(QUERY_STRING, Utils.map(1, id));
        if (result == null) {
            throw new IllegalArgumentException("AttractionType introuvable");
        } else {
            return result;
        }
    }

    public IdResponse update(int id, Map<String, Object> updates) {
        if (updates == null || updates.isEmpty()) {
            return new IdResponse(id);
        }
        StringBuilder sql = new StringBuilder("UPDATE AttractionType SET ");
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
