package com.b2z.dao;

import com.b2z.model.Attraction;
import com.b2z.model.HoraireOuverture;
import com.b2z.service.ThemeParkAPI;
import com.b2z.utils.DBRequest;
import com.b2z.utils.Utils;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


import java.util.List;

public class AttractionDAO implements DAOInterface<Attraction, AttractionDAO.AttractionProps> {

    public record AttractionProps(
            @NotNull @Size(min = 3) String nom,
            @NotNull Integer typeId,
            @NotNull @Min(1) Integer tailleMin,
            @NotNull @Min(1) Integer tailleMinAdulte
    ) {}

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
    public Attraction create(@NotNull AttractionProps props) {
        final String QUERY_STRING = """
            INSERT INTO Attraction (nom, type_id, taille_min, taille_min_adulte)
            VALUES (?, ?, ?, ?)
        """;
         final List<Attraction> result =  DBRequest.execute(
            QUERY_STRING,
            Utils.map(
                    1, props.nom(),
                    2, props.typeId(),
                    3, props.tailleMin(),
                    4, props.tailleMinAdulte()
            ),
            Attraction::fromResultSet
        );

        Attraction attraction = result.get(0);
        ThemeParkAPI.getInstance().addAttraction(props.nom(), Integer.toString(attraction.getId()));
        return attraction;
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

