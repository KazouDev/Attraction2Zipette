package com.b2z.dao;

import com.b2z.model.Attraction;
import com.b2z.model.HoraireOuverture;
import com.b2z.utils.DBRequest;
import com.b2z.utils.Utils;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

public class AttractionDAO implements DAOInterface {

    public List<Attraction> findAll() {
        final String QUERY_STRING = """
            SELECT a.*, type.nom as type 
            FROM Attraction a
            LEFT JOIN AttractionType type 
            ON a.type_id = type.id    
        """;
        return DBRequest.execute(QUERY_STRING, null, Attraction::fromResultSet);
    }

    public Attraction findById(@NotNull int id) {
        final String QUERY_STRING = """
            SELECT a.*, type.nom as type 
            FROM Attraction a
            LEFT JOIN AttractionType type 
            ON a.type_id = type.id
            WHERE a.id = ?
        """;
        List<Attraction> result = DBRequest.execute(
                QUERY_STRING,
                Utils.map(1, id),
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
        final String QUERY_STRING ="""
            DELETE FROM Attraction 
            WHERE id = ? 
            RETURNING *  
        """;
        List<Attraction> result = DBRequest.execute(QUERY_STRING, Utils.map(1, id), Attraction::fromResultSet);
        return result.get(0);
    }

    private List<HoraireOuverture> getHoraireOuvertureFromAttraction(int attractionId) {
        final String QUERY_STRING = """
                    SELECT * FROM HoraireOuverture
                    WHERE attraction_id = ?
                    ORDER BY jour_semaine ASC
                """;
        return DBRequest.execute(QUERY_STRING, Utils.map(1, attractionId), HoraireOuverture::fromResultSet);
    }

}

