package com.b2z.dao;

import com.b2z.model.Personnage;
import com.b2z.model.Programmation;
import com.b2z.utils.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.*;
import java.util.stream.Collectors;

public class PersonnageDAO implements DAOInterface<Personnage, PersonnageDAO.PersonnageProps> {

    public record PersonnageProps(
            @NotNull @Size(min = 3, max = 30) String nom
            ) {}

    @Override
    public List<Personnage> findAll() {
        final String QUERY_STRING = """
            SELECT %s
            FROM Personnage %s
            ORDER BY %s.nom ASC
        """.formatted(
                Personnage.SELECT_COLUMNS,
                Personnage.ALIAS_NAME,
                Personnage.ALIAS_NAME
        );
        return DBRequest.executeSelect(QUERY_STRING, null, Personnage::fromResultSet);
    }

    @Override
    public Personnage findById(int id) {
        final String QUERY_STRING = """
            SELECT %s
            FROM Personnage %s
            WHERE %s.id = ?
        """.formatted(
                Personnage.SELECT_COLUMNS,
                Personnage.ALIAS_NAME,
                Personnage.ALIAS_NAME
        );

        List<Personnage> result = DBRequest.executeSelect(
                QUERY_STRING,
                Utils.map(1, id),
                Personnage::fromResultSet
        );

        if (result == null || result.isEmpty()) {
            throw new IllegalArgumentException("Personnage introuvable");
        }

        return result.get(0);
    }

    @Override
    public IdResponse create(PersonnageProps props) {
        final String CHECK_QUERY = """
            SELECT COUNT(*) as count
            FROM Personnage
            WHERE nom = ?
        """;

        List<Map<String, Object>> existsResult = DBRequest.executeSelect(
                CHECK_QUERY,
                Utils.map(1, props.nom()),
                rs -> {
                    try {
                        return Map.of("count", rs.getLong("count"));
                    } catch (Exception e) {
                        return null;
                    }
                }
        );

        if (existsResult != null && !existsResult.isEmpty()) {
            Long count = (Long) existsResult.get(0).get("count");
            if (count != null && count > 0) {
                throw new IllegalArgumentException("Un personnage avec ce nom existe déjà");
            }
        }

        final String QUERY_STRING = """
            INSERT INTO Personnage (nom)
            VALUES (?)
        """;

        IdResponse result = DBRequest.executeCommand(
                QUERY_STRING,
                Utils.map(1, props.nom())
        );

        if (result == null) {
            throw new IllegalArgumentException("Erreur lors de la création du personnage");
        }

        return result;
    }

    @Override
    public IdResponse delete(int id) {
        final String CHECK_QUERY = """
            SELECT COUNT(*) as count
            FROM Personnage
            WHERE id = ?
        """;

        List<Map<String, Object>> existsResult = DBRequest.executeSelect(
                CHECK_QUERY,
                Utils.map(1, id),
                rs -> {
                    try {
                        return Map.of("count", rs.getLong("count"));
                    } catch (Exception e) {
                        return null;
                    }
                }
        );

        if (existsResult == null || existsResult.isEmpty()) {
            throw new IllegalArgumentException("Personnage introuvable");
        }

        Long count = (Long) existsResult.get(0).get("count");
        if (count == null || count == 0) {
            throw new IllegalArgumentException("Personnage introuvable");
        }

        final String QUERY_STRING = """
            DELETE FROM Personnage
            WHERE id = ?
        """;

        IdResponse result = DBRequest.executeCommand(
                QUERY_STRING,
                Utils.map(1, id)
        );

        if (result == null) {
            throw new IllegalArgumentException("Erreur lors de la suppression du personnage");
        }

        return result;
    }

    public IdResponse update(int id, Map<String, Object> updates) {
        if (updates.isEmpty()) {
            throw new IllegalArgumentException("Aucune mise à jour fournie");
        }

        findById(id);

        if (updates.containsKey("nom")) {
            final String CHECK_QUERY = """
                SELECT COUNT(*) as count
                FROM Personnage
                WHERE nom = ? AND id != ?
            """;

            List<Map<String, Object>> existsResult = DBRequest.executeSelect(
                    CHECK_QUERY,
                    Utils.map(1, updates.get("nom"), 2, id),
                    rs -> {
                        try {
                            return Map.of("count", rs.getLong("count"));
                        } catch (Exception e) {
                            return null;
                        }
                    }
            );

            if (existsResult != null && !existsResult.isEmpty()) {
                Long count = (Long) existsResult.get(0).get("count");
                if (count != null && count > 0) {
                    throw new IllegalArgumentException("Un personnage avec ce nom existe déjà");
                }
            }
        }

        StringBuilder queryBuilder = new StringBuilder("UPDATE Personnage SET ");
        int paramIndex = 1;
        Map<Integer, Object> params = new HashMap<>();

        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            queryBuilder.append(entry.getKey()).append(" = ?");
            if (paramIndex < updates.size()) {
                queryBuilder.append(", ");
            }
            params.put(paramIndex++, entry.getValue());
        }

        queryBuilder.append(" WHERE id = ?");
        params.put(paramIndex, id);

        String QUERY_STRING = queryBuilder.toString();
        IdResponse result = DBRequest.executeCommand(QUERY_STRING, params);

        if (result == null) {
            throw new IllegalArgumentException("Erreur lors de la mise à jour du personnage");
        }

        return result;
    }
}
