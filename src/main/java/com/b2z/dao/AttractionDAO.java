package com.b2z.dao;

import com.b2z.model.Attraction;
import com.b2z.model.HoraireOuverture;
import com.b2z.utils.DBManager;
import com.b2z.utils.DBRequest;
import com.b2z.utils.JourSemaine;
import jakarta.validation.constraints.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttractionDAO implements DAOInterface {

    public List<Attraction> findAll() {
        return DBRequest.execute(
                "SELECT a.*, type.nom as type FROM Attraction a LEFT JOIN AttractionType type ON a.type_id = type.id",
                null,
                Attraction::fromResultSet
        );
    }

    public Attraction findById(@NotNull int id) {
        final String QUERY_STRING = """
                    SELECT a.*, type.nom as type FROM Attraction a
                    LEFT JOIN AttractionType type ON a.type_id = type.id
                    WHERE a.id = ?
                """;
        Map<Integer, Object> params = new HashMap<>();
        params.put(1, id);
        List<Attraction> result = DBRequest.execute(
                QUERY_STRING,
                params,
                Attraction::fromResultSet
        );
        Attraction attraction = result.get(0);
        if (attraction == null) {
            return null;
        }
        attraction.setHoraireOuvertures(this.getHoraireOuvertureFromAttraction(attraction.getId()));
        return attraction;
    }

    @Override
    public Attraction create(@NotNull Map props) {
        return null;
    }


    @Override
    public Attraction delete(@NotNull int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBManager.getInstance().getConnection();
            stmt = conn.prepareStatement("DELETE FROM Attraction WHERE id = ? RETURNING *;");
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                Attraction attraction = Attraction.fromResultSet(rs);
                if (attraction == null) {
                    return null;
                }
                attraction.setHoraireOuvertures(this.getHoraireOuvertureFromAttraction(attraction.getId()));
                return attraction;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.getInstance().cleanup(conn, stmt, null);
        }
        return null;
    }

    private List<HoraireOuverture> getHoraireOuvertureFromAttraction(int attractionId) {
        final String QUERY_STRING = """
                    SELECT * FROM HoraireOuverture
                    WHERE attraction_id = ?
                    ORDER BY jour_semaine ASC
                """;

        Map<Integer, Object> params = new HashMap<>();
        params.put(1, attractionId);
        List<HoraireOuverture> horaires = DBRequest.execute(QUERY_STRING, params, HoraireOuverture::fromResultSet);

        return horaires;
}

}

