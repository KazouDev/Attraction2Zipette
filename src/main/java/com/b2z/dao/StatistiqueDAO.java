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

    public List<PersonnageWithActivity> getTopPersonnagesActivity(int topN){
        final String QUERY = """
            SELECT p.id AS personnage_id, p.nom AS personnage_nom, SUM(a.activity_duration) AS total_activity
            FROM (
                SELECT rp.personnage_id AS personnage_id,
                       TIMESTAMPDIFF(MINUTE, rp.heure_debut, rp.heure_fin) AS activity_duration
                FROM RencontrePersonnage rp
                UNION ALL
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

    public List<SpectacleWithDuration> getSpectaclesRankingByDurationForDay(int jourSemaine) {
        final String QUERY = """
            SELECT s.id AS spectacle_id, 
                   s.titre AS spectacle_titre,
                   pr.jour_semaine,
                   TIMESTAMPDIFF(MINUTE, pr.heure_debut, pr.heure_fin) AS duration_minutes
            FROM Spectacle s
            JOIN Programmation pr ON s.id = pr.spectacle_id
            WHERE pr.jour_semaine = ?
            ORDER BY duration_minutes DESC, s.titre ASC
            """;

        return DBRequest.executeSelect(QUERY, Utils.map(1, jourSemaine), rs -> {
            try {
                int spectacleId = rs.getInt("spectacle_id");
                String spectacleTitre = rs.getString("spectacle_titre");
                int jour = rs.getInt("jour_semaine");
                int durationMinutes = rs.getInt("duration_minutes");
                return new SpectacleWithDuration(spectacleId, spectacleTitre, jour, durationMinutes);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public List<SpectacleWithDuration> getSpectaclesRankingByDurationAllDays() {
        final String QUERY = """
            SELECT s.id AS spectacle_id, 
                   s.titre AS spectacle_titre,
                   pr.jour_semaine,
                   TIMESTAMPDIFF(MINUTE, pr.heure_debut, pr.heure_fin) AS duration_minutes
            FROM Spectacle s
            JOIN Programmation pr ON s.id = pr.spectacle_id
            ORDER BY pr.jour_semaine ASC, duration_minutes DESC, s.titre ASC
            """;

        return DBRequest.executeSelect(QUERY, null, rs -> {
            try {
                int spectacleId = rs.getInt("spectacle_id");
                String spectacleTitre = rs.getString("spectacle_titre");
                int jour = rs.getInt("jour_semaine");
                int durationMinutes = rs.getInt("duration_minutes");
                return new SpectacleWithDuration(spectacleId, spectacleTitre, jour, durationMinutes);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public record SpectacleWithDuration(
            int spectacleId,
            String spectacleTitre,
            int jourSemaine,
            int durationMinutes
    ) {}
}
