package com.b2z.dao;

import com.b2z.model.Personnage;
import com.b2z.model.PersonnageWithActivity;
import com.b2z.model.RencontrePersonnage;
import com.b2z.model.Spectacle;
import com.b2z.utils.DBRequest;
import com.b2z.utils.Utils;

import java.util.List;

public class StatistiqueDAO {
    public StatistiqueDAO() {

    }

    // java
    public List<PersonnageWithActivity> getTopPersonnagesActivity(int topN){
        final String QUERY = """
            SELECT p.id AS personnage_id, p.nom AS personnage_nom, SUM(a.activity_duration) AS total_activity
            FROM (
                -- Durées des rencontres
                SELECT rp.personnage_id AS personnage_id,
                       TIMESTAMPDIFF(MINUTE, rp.heure_debut, rp.heure_fin) AS activity_duration
                FROM RencontrePersonnage rp
                UNION ALL
                -- Durées des programmations de spectacles (via la table de liaison)
                SELECT sp.personnage_id AS personnage_id,
                       TIMESTAMPDIFF(MINUTE, pr.heure_debut, pr.heure_fin) AS activity_duration
                FROM Programmation pr
                JOIN Spectacle s ON pr.spectacle_id = s.id
                JOIN SpectaclePersonnage sp ON sp.spectacle_id = s.id
            ) a
            JOIN Personnage p ON a.personnage_id = p.id
            GROUP BY p.id, p.nom
            ORDER BY total_activity DESC
            LIMIT ?
            """;

        System.out.println(QUERY);
        return DBRequest.executeSelect(QUERY, Utils.map(1, topN), PersonnageWithActivity::fromResultSet);
    }


}


