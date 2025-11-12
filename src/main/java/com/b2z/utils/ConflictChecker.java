package com.b2z.utils;

import com.b2z.dao.SpectacleDAO;
import com.b2z.model.Programmation;
import com.b2z.model.Spectacle;
import jakarta.validation.constraints.NotNull;

import java.sql.Time;
import java.util.List;
import java.util.Map;

public class ConflictChecker {
    static final int MINUTES_BETWEEN_DIFFERENT_LOCATIONS = 30;

    public static boolean heuresSeChevauche(Time debut1, Time fin1, Time debut2, Time fin2) {
        return !(fin1.before(debut2) || fin1.equals(debut2) ||
                debut1.after(fin2) || debut1.equals(fin2));
    }

    public static long calculateMinutesBetween(Time debut1, Time fin1, Time debut2, Time fin2) {
        long debut1Min = debut1.getTime() / 60000;
        long fin1Min = fin1.getTime() / 60000;
        long debut2Min = debut2.getTime() / 60000;
        long fin2Min = fin2.getTime() / 60000;

        if (fin1Min <= debut2Min) {
            return debut2Min - fin1Min;
        }
        else if (fin2Min <= debut1Min) {
            return debut1Min - fin2Min;
        }
        else {
            return 0;
        }
    }

    public static void checkConflictsWithSpectacles(
            int personnageId,
            int jourSemaine,
            Time heureDebut,
            Time heureFin,
            int lieuId
    ) {
        final String QUERY_SPECTACLES = """
            SELECT p.jour_semaine, p.heure_debut, p.heure_fin, s.titre, l.id as lieu_id
            FROM Programmation p
            INNER JOIN Spectacle s ON p.spectacle_id = s.id
            INNER JOIN Lieu l ON s.lieu_id = l.id
            INNER JOIN SpectaclePersonnage sp ON sp.spectacle_id = s.id
            WHERE sp.personnage_id = ? AND p.jour_semaine = ?
        """;

        List<Map<String, Object>> spectacles = DBRequest.executeSelect(
                QUERY_SPECTACLES,
                Utils.map(1, personnageId, 2, jourSemaine),
                rs -> {
                    try {
                        return Map.of(
                                "jourSemaine", rs.getInt("jour_semaine"),
                                "heureDebut", rs.getTime("heure_debut"),
                                "heureFin", rs.getTime("heure_fin"),
                                "titre", rs.getString("titre"),
                                "lieuId", rs.getInt("lieu_id")
                        );
                    } catch (Exception e) {
                        return null;
                    }
                }
        );

        for (Map<String, Object> spectacle : spectacles) {
            Time spectacleDebut = (Time) spectacle.get("heureDebut");
            Time spectacleFin = (Time) spectacle.get("heureFin");
            String spectacleTitre = (String) spectacle.get("titre");
            int spectacleLieuId = (Integer) spectacle.get("lieuId");

            if (heuresSeChevauche(heureDebut, heureFin, spectacleDebut, spectacleFin)) {
                throw new IllegalArgumentException(
                        "Conflit : le personnage est déjà dans le spectacle '"
                        + spectacleTitre + "' à ce créneau"
                );
            }

            if (lieuId != spectacleLieuId) {
                long minutesBetween = calculateMinutesBetween(
                        heureDebut, heureFin,
                        spectacleDebut, spectacleFin
                );

                if (minutesBetween < MINUTES_BETWEEN_DIFFERENT_LOCATIONS) {
                    throw new IllegalArgumentException(
                            "Le personnage nécessite 30 min de battement entre l'activité "
                            + "et le spectacle '" + spectacleTitre + "' (lieux différents). "
                            + "Temps disponible : " + minutesBetween + " min"
                    );
                }
            }
        }
    }

    public static void checkConflictsWithRencontres(
            int personnageId,
            int jourSemaine,
            Time heureDebut,
            Time heureFin,
            int lieuId,
            Integer excludeRencontreId
    ) {
        String QUERY_RENCONTRES = """
            SELECT id, jour_semaine, heure_debut, heure_fin, lieu_id
            FROM RencontrePersonnage
            WHERE personnage_id = ? AND jour_semaine = ?
        """;

        Map<Integer, Object> params = new java.util.HashMap<>();
        params.put(1, personnageId);
        params.put(2, jourSemaine);

        if (excludeRencontreId != null) {
            QUERY_RENCONTRES += " AND id != ?";
            params.put(3, excludeRencontreId);
        }

        List<Map<String, Object>> rencontres = DBRequest.executeSelect(
                QUERY_RENCONTRES,
                params,
                rs -> {
                    try {
                        return Map.of(
                                "id", rs.getInt("id"),
                                "jourSemaine", rs.getInt("jour_semaine"),
                                "heureDebut", rs.getTime("heure_debut"),
                                "heureFin", rs.getTime("heure_fin"),
                                "lieuId", rs.getInt("lieu_id")
                        );
                    } catch (Exception e) {
                        return null;
                    }
                }
        );

        for (Map<String, Object> rencontre : rencontres) {
            Time rencontreDebut = (Time) rencontre.get("heureDebut");
            Time rencontreFin = (Time) rencontre.get("heureFin");
            int rencontreLieuId = (Integer) rencontre.get("lieuId");

            if (heuresSeChevauche(heureDebut, heureFin, rencontreDebut, rencontreFin)) {
                throw new IllegalArgumentException(
                        "Conflit : le personnage a déjà une rencontre prévue à ce créneau"
                );
            }

            if (lieuId != rencontreLieuId) {
                long minutesBetween = calculateMinutesBetween(
                        heureDebut, heureFin,
                        rencontreDebut, rencontreFin
                );

                if (minutesBetween < MINUTES_BETWEEN_DIFFERENT_LOCATIONS) {
                    throw new IllegalArgumentException(
                            "Le personnage nécessite 30 min de battement entre ses rencontres "
                            + "(lieux différents). Temps disponible : " + minutesBetween + " min"
                    );
                }
            }
        }
    }

    public static void checkAllConflicts(
            int personnageId,
            int jourSemaine,
            Time heureDebut,
            Time heureFin,
            int lieuId,
            Integer excludeRencontreId
    ) {
        checkConflictsWithSpectacles(personnageId, jourSemaine, heureDebut, heureFin, lieuId);
        checkConflictsWithRencontres(personnageId, jourSemaine, heureDebut, heureFin, lieuId, excludeRencontreId);
    }

    public static void checkConflictsForPersonnageInSpectacle(
            List<Programmation> spectacleProgrammations,
            int spectacleLieuId,
            int personnageId
    ) {
        for (Programmation prog : spectacleProgrammations) {
            checkAllConflicts(
                    personnageId,
                    prog.getJourSemaine().getValeur(),
                    prog.getHeureOuverture(),
                    prog.getHeureFermeture(),
                    spectacleLieuId,
                    null
            );
        }
    }

    public static boolean respecteBattement(@NotNull Time time, @NotNull Time time1, @NotNull Time heureOuverture, @NotNull Time heureFermeture) {
        long minutesBetween = calculateMinutesBetween(time, time1, heureOuverture, heureFermeture);
        return minutesBetween >= MINUTES_BETWEEN_DIFFERENT_LOCATIONS;
    }
}

