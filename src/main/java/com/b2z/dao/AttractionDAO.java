package com.b2z.dao;

import com.b2z.model.Attraction;
import com.b2z.model.AttractionType;
import com.b2z.model.HoraireOuverture;
import com.b2z.service.ThemeParkAPI;
import com.b2z.utils.DBRequest;
import com.b2z.utils.Utils;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class AttractionDAO implements DAOInterface<Attraction, AttractionDAO.AttractionProps> {

    public record AttractionProps(
            @NotNull @Size(min = 3, max = 30) String nom,
            @NotNull Integer typeId,
            @NotNull @Min(1) @Max(200) Integer tailleMin,
            @NotNull @Min(1) @Max(200) Integer tailleMinAdulte
    ) {}

    public record HoraireOuvertureProps(
            @NotNull @Min(0) @Max(6) Integer jourSemaine,
            @NotNull @Size(min = 5) String heureOuverture,
            @NotNull @Size(min = 5) String heureFermeture
    ) {}

    public List<Attraction> findAll() {
        final String QUERY_ATTRACTIONS = """
            SELECT %s
            FROM Attraction attraction
            LEFT JOIN AttractionType attraction_type ON attraction.type_id = attraction_type.id
            ORDER BY attraction.id
        """.formatted(Attraction.getSelectColumns());

        List<Attraction> attractions = DBRequest.executeSelect(QUERY_ATTRACTIONS, null, Attraction::fromResultSet);

        if (attractions == null || attractions.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Integer, Attraction> attractionsById = new HashMap<>();
        for (Attraction attraction : attractions) {
            attractionsById.put(attraction.getId(), attraction);
        }

        String attractionIds = attractions.stream()
                .map(a -> String.valueOf(a.getId()))
                .collect(java.util.stream.Collectors.joining(","));

        final String QUERY_HORAIRES = """
            SELECT %s, horaire_ouverture.attraction_id
            FROM HoraireOuverture %s
            WHERE horaire_ouverture.attraction_id IN (%s)
            ORDER BY horaire_ouverture.attraction_id, horaire_ouverture.jour_semaine ASC
        """.formatted(
                HoraireOuverture.SELECT_COLUMNS,
                HoraireOuverture.ALIAS_NAME,
                attractionIds
        );

        DBRequest.executeSelect(QUERY_HORAIRES, null, rs -> {
            try {
                int attractionId = rs.getInt("attraction_id");
                HoraireOuverture horaire = HoraireOuverture.fromResultSet(rs);

                if (horaire != null) {
                    Attraction attraction = attractionsById.get(attractionId);
                    if (attraction != null) {
                        attraction.addHoraireOuvertureIfNotPresent(horaire);
                    }
                }
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });


        return attractions;
    }

    public Attraction findById(@NotNull int id) {
        final String QUERY_ATTRACTION = """
            SELECT %s
            FROM Attraction attraction
            LEFT JOIN AttractionType attraction_type ON attraction.type_id = attraction_type.id
            WHERE attraction.id = ?
        """.formatted(Attraction.getSelectColumns());

        List<Attraction> attractions = DBRequest.executeSelect(QUERY_ATTRACTION, Utils.map(1, id), Attraction::fromResultSet);

        if (attractions == null || attractions.isEmpty()) {
            throw new IllegalArgumentException("Attraction introuvable");
        }

        Attraction attraction = attractions.get(0);

        final String QUERY_HORAIRES = """
            SELECT %s
            FROM HoraireOuverture %s
            WHERE %s.attraction_id = ?
            ORDER BY %s.jour_semaine ASC
        """.formatted(
                HoraireOuverture.SELECT_COLUMNS,
                HoraireOuverture.ALIAS_NAME,
                HoraireOuverture.ALIAS_NAME,
                HoraireOuverture.ALIAS_NAME
        );

        List<HoraireOuverture> horaires = DBRequest.executeSelect(
                QUERY_HORAIRES,
                Utils.map(1, id),
                HoraireOuverture::fromResultSet
        );

        if (horaires != null) {
            attraction.getHoraireOuvertures().addAll(horaires);
        }

        return attraction;
    }

    @Override
    public IdResponse create(@NotNull AttractionProps props) {
        final String QUERY_TYPE_STRING = """
            SELECT %s FROM AttractionType %s
            WHERE %s.id = ?
        """.formatted(
            AttractionType.SELECT_COLUMNS,
            AttractionType.ALIAS_NAME,
            AttractionType.ALIAS_NAME
        );

        final String QUERY_STRING = """
            INSERT INTO Attraction (nom, type_id, taille_min, taille_min_adulte)
            VALUES (?, ?, ?, ?)
        """;

        System.out.println(props.typeId());

         List<AttractionType> type = DBRequest.executeSelect(
            QUERY_TYPE_STRING,
            Utils.map(1, props.typeId()),
            AttractionType::fromResultSet
         );

         if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("Type d'attraction invalide");
         }

         final IdResponse result = DBRequest.executeCommand(
            QUERY_STRING,
            Utils.map(
                    1, props.nom(),
                    2, props.typeId(),
                    3, props.tailleMin(),
                    4, props.tailleMinAdulte()
            )
        );
        ThemeParkAPI.getInstance().addAttraction(props.nom(), Integer.toString(result.id()));
        return result;
    }

    @Override
    public IdResponse delete(@NotNull int id) {
        final String QUERY_STRING ="""
            DELETE FROM Attraction 
            WHERE id = ?
        """;

        IdResponse result = DBRequest.executeCommand(QUERY_STRING, Utils.map(1, id));
        if (result == null) {
            throw new IllegalArgumentException("Attraction introuvable");
        } else {
            ThemeParkAPI.getInstance().removeAttraction(Integer.toString(id));
            return result;
        }
    }

    public IdResponse updatePartial(int id, Map<String, Object> updates) {
        if (updates == null || updates.isEmpty()) {
            return new IdResponse(id);
        }

        StringBuilder sql = new StringBuilder("UPDATE Attraction SET ");
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


    // TODO: Optimisation : On ne supprimer plus toutes les lignes, mais seulement celles qui ont été modifiées.
    public IdResponse updateHoraireOuverture(int attractionId, List<HoraireOuvertureProps> horaires) {
        final String DELETE_QUERY = """
            DELETE FROM HoraireOuverture
            WHERE attraction_id = ?
        """;

        final String INSERT_QUERY = """
            INSERT INTO HoraireOuverture (attraction_id, jour_semaine, heure_ouverture, heure_fermeture)
            VALUES (?, ?, ?, ?)
        """;
        HashSet<HoraireOuvertureProps> setHoraires = new HashSet<>(horaires);

        if (setHoraires.size() != horaires.size()) {
            throw new IllegalArgumentException("Les horaires d'ouverture contiennent des doublons.");
        }

        for (HoraireOuvertureProps props : setHoraires) {
            if (props.heureOuverture().compareTo(props.heureFermeture()) >= 0) {
                throw new IllegalArgumentException("L'heure d'ouverture doit être avant l'heure de fermeture pour le jour " + props.jourSemaine());
            }
        }

        DBRequest.executeCommand(DELETE_QUERY, Utils.map(1, attractionId));

        for (HoraireOuvertureProps horaire : setHoraires) {
            DBRequest.executeCommand(
                INSERT_QUERY,
                Utils.map(
                    1, attractionId,
                    2, horaire.jourSemaine(),
                    3, horaire.heureOuverture(),
                    4, horaire.heureFermeture()
                )
            );
        }

        return new IdResponse(attractionId);
    }
}
