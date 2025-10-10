package com.b2z.dao;

import com.b2z.model.Attraction;
import com.b2z.model.HoraireOuverture;
import com.b2z.utils.DBManager;
import com.b2z.utils.DBRequest;
import com.b2z.utils.JourSemaine;
import jakarta.validation.constraints.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AttractionDAO implements DAOInterface {

    public List<Attraction> findAll() {


        List<Attraction> attractions = DBRequest.execute(
                "SELECT a.*, type.nom as type FROM Attraction a LEFT JOIN AttractionType type ON a.type_id = type.id",
                null,
                Attraction::fromResultSet
        );

        return attractions;
    }

    public Attraction findById(@NotNull int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBManager.getInstance().getConnection();
            String sql = "SELECT a.*, type.nom as type FROM Attraction a " +
                        "LEFT JOIN AttractionType type ON a.type_id = type.id " +
                        "WHERE a.id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                Attraction attraction = Attraction.fromResultSet(rs);
                if (attraction == null){
                    return null;
                }
                attraction.setHoraireOuvertures(this.getHoraireOuvertureFromAttraction(attraction.getId()));
                return attraction;
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.getInstance().cleanup(conn, stmt, rs);
        }

        return null;
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
                if (attraction == null){
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
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<HoraireOuverture> horaires = new ArrayList<>();

        try {
            conn = DBManager.getInstance().getConnection();
            String sql = "SELECT * FROM HoraireOuverture WHERE attraction_id = ?";

            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, attractionId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                int jour = rs.getInt("jour_semaine");
                if (jour < 0 || jour > 6){
                    continue;
                }
                JourSemaine jourSemaine = JourSemaine.fromInt(rs.getInt("jour_semaine"));
                Time ouverture = rs.getTime("heure_ouverture");
                Time fermeture = rs.getTime("heure_fermeture");
                horaires.add(new HoraireOuverture(jourSemaine, ouverture, fermeture));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.getInstance().cleanup(conn, stmt, rs);
        }

        return horaires;
    }

}

