package com.b2z.dao;

import com.b2z.model.*;
import com.b2z.utils.ConflictChecker;
import com.b2z.utils.DBRequest;
import com.b2z.utils.Utils;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.sql.Time;
import java.util.*;

public class SpectacleDAO implements DAOInterface<Spectacle, SpectacleDAO.SpectacleProps> {

    public record SpectacleProps(
            @NotNull @Size(min = 3, max = 30) String titre,
            @NotNull int lieuId
    ) {}

    public record ProgrammationProps(
            @NotNull @Min(0) @Max(6) Integer jourSemaine,
            @NotNull Time heureOuverture,
            @NotNull Time heureFermeture
    ) {}

    private record ProgrammationWithSpectacle(Programmation programmation, Spectacle spectacle) {
        static ProgrammationWithSpectacle fromResultSet(java.sql.ResultSet rs) {
            Programmation programmation = Programmation.fromResultSet(rs);
            Spectacle spectacle = Spectacle.fromResultSet(rs);
            if (programmation != null && spectacle != null) {
                return new ProgrammationWithSpectacle(programmation, spectacle);
            } else {
                return null;
            }
        }
    }

    @Override
    public List<Spectacle> findAll() {
        final String QUERY_SPECTACLES = """
            SELECT %s
            FROM Spectacle %s
            INNER JOIN Lieu %s ON %s.lieu_id = %s.id
            ORDER BY %s.id
        """.formatted(
                Spectacle.SELECT_COLUMNS,
                Spectacle.ALIAS_NAME,
                Lieu.ALIAS_NAME, Spectacle.ALIAS_NAME, Lieu.ALIAS_NAME,
                Spectacle.ALIAS_NAME
        );

        List<Spectacle> spectacles = DBRequest.executeSelect(QUERY_SPECTACLES, null, Spectacle::fromResultSet);

        if (spectacles == null || spectacles.isEmpty()) {
            return new ArrayList<>();
        }

        HashMap<Integer, Spectacle> spectaclesById = new HashMap<>();
        for (Spectacle spectacle : spectacles) {
            spectaclesById.put(spectacle.getId(), spectacle);
        }

        String spectacleIds = spectacles.stream()
                .map(s -> String.valueOf(s.getId()))
                .collect(java.util.stream.Collectors.joining(","));

        final String QUERY_PROGRAMMATIONS = """
            SELECT %s, programmation.spectacle_id
            FROM Programmation %s
            WHERE programmation.spectacle_id IN (%s)
            ORDER BY programmation.spectacle_id, %s.jour_semaine ASC
        """.formatted(
                Programmation.SELECT_COLUMNS,
                Programmation.ALIAS_NAME,
                spectacleIds,
                Programmation.ALIAS_NAME
        );

        DBRequest.executeSelect(QUERY_PROGRAMMATIONS, null, rs -> {
            try {
                int spectacleId = rs.getInt("spectacle_id");
                Programmation prog = Programmation.fromResultSet(rs);
                if (prog != null) {
                    Spectacle spec = spectaclesById.get(spectacleId);
                    if (spec != null) {
                        spec.addProgrammationIfNotExists(prog);
                    }
                }
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });

        final String QUERY_PERSONNAGES = """
            SELECT %s, sp.spectacle_id
            FROM Personnage %s
            INNER JOIN SpectaclePersonnage sp ON sp.personnage_id = %s.id
            WHERE sp.spectacle_id IN (%s)
            ORDER BY sp.spectacle_id, %s.id ASC
        """.formatted(
                Personnage.SELECT_COLUMNS,
                Personnage.ALIAS_NAME,
                Personnage.ALIAS_NAME,
                spectacleIds,
                Personnage.ALIAS_NAME
        );

        DBRequest.executeSelect(QUERY_PERSONNAGES, null, rs -> {
            try {
                int spectacleId = rs.getInt("spectacle_id");
                Personnage perso = Personnage.fromResultSet(rs);
                if (perso != null) {
                    Spectacle spec = spectaclesById.get(spectacleId);
                    if (spec != null) {
                        spec.addPersonnageIfNotExists(perso);
                    }
                }
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });

        return spectacles;
    }

    @Override
    public Spectacle findById(int id) {
        final String QUERY_SPECTACLE = """
            SELECT %s
            FROM Spectacle %s
            INNER JOIN Lieu %s ON %s.lieu_id = %s.id
            WHERE %s.id = ?
        """.formatted(
                Spectacle.SELECT_COLUMNS,
                Spectacle.ALIAS_NAME,
                Lieu.ALIAS_NAME, Spectacle.ALIAS_NAME, Lieu.ALIAS_NAME,
                Spectacle.ALIAS_NAME
        );

        List<Spectacle> spectacles = DBRequest.executeSelect(QUERY_SPECTACLE, Utils.map(1, id), Spectacle::fromResultSet);

        if (spectacles == null || spectacles.isEmpty()) {
            throw new IllegalArgumentException("Spectacle introuvable");
        }

        Spectacle spectacle = spectacles.get(0);

        final String QUERY_PROGRAMMATIONS = """
            SELECT %s
            FROM Programmation %s
            WHERE %s.spectacle_id = ?
            ORDER BY %s.jour_semaine ASC
        """.formatted(
                Programmation.SELECT_COLUMNS,
                Programmation.ALIAS_NAME,
                Programmation.ALIAS_NAME,
                Programmation.ALIAS_NAME
        );

        List<Programmation> programmations = DBRequest.executeSelect(
                QUERY_PROGRAMMATIONS,
                Utils.map(1, id),
                Programmation::fromResultSet
        );

        final String QUERY_PERSONNAGES = """
            SELECT %s
            FROM Personnage %s
            INNER JOIN SpectaclePersonnage sp ON sp.personnage_id = %s.id
            WHERE sp.spectacle_id = ?
            ORDER BY %s.id ASC
        """.formatted(
                Personnage.SELECT_COLUMNS,
                Personnage.ALIAS_NAME,
                Personnage.ALIAS_NAME,
                Personnage.ALIAS_NAME
        );

        List<Personnage> personnages = DBRequest.executeSelect(
                QUERY_PERSONNAGES,
                Utils.map(1, id),
                Personnage::fromResultSet
        );

        spectacle.setProgrammations(programmations != null ? programmations : new ArrayList<>());
        spectacle.setPersonnages(personnages != null ? personnages : new ArrayList<>());

        return spectacle;
    }

    @Override
    public IdResponse create(SpectacleProps props) {
        final String QUERY_CHECK = """
            SELECT %s
            FROM Lieu %s
            WHERE %s.id = ?
        """.formatted(Lieu.SELECT_COLUMNS, Lieu.ALIAS_NAME, Lieu.ALIAS_NAME);

        List<Lieu> checkResult = DBRequest.executeSelect(QUERY_CHECK, Utils.map(1, props.lieuId()), Lieu::fromResultSet);
        if (checkResult == null || checkResult.isEmpty()) {
            throw new IllegalArgumentException("Le lieu spécifié n'existe pas");
        }

        final String QUERY_STRING = """
            INSERT INTO Spectacle (titre, lieu_id)
            VALUES (?, ?)
        """;
        IdResponse result = DBRequest.executeCommand(QUERY_STRING, Utils.map(1, props.titre(), 2, props.lieuId()));
        if (result == null) {
            throw new IllegalArgumentException("Erreur lors de la création du spectacle");
        }
        return result;
    }

    @Override
    public IdResponse delete(int id) {
        final String QUERY_STRING = """
            DELETE FROM Spectacle
            WHERE id = ?
        """;
        IdResponse result = DBRequest.executeCommand(QUERY_STRING, Utils.map(1, id));
        if (result == null) {
            throw new IllegalArgumentException("Spectacle introuvable");
        } else {
            return result;
        }
    }

    public IdResponse update(int id, Map<String, Object> updates) {
        StringBuilder queryBuilder = new StringBuilder("UPDATE Spectacle SET ");
        int paramIndex = 1;
        java.util.Map<Integer, Object> params = new java.util.HashMap<>();

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
            throw new IllegalArgumentException("Erreur lors de la mise à jour du spectacle");
        }
        return result;
    }


    public IdResponse addProgrammation(int spectacleId, ProgrammationProps props) {
        final String CHECK_SPECTACLE = """
            SELECT %s
            FROM Spectacle %s
            INNER JOIN Lieu %s ON %s.lieu_id = %s.id
            WHERE %s.id = ?
        """.formatted(Spectacle.SELECT_COLUMNS, Spectacle.ALIAS_NAME,
                Lieu.ALIAS_NAME, Spectacle.ALIAS_NAME, Lieu.ALIAS_NAME, Spectacle.ALIAS_NAME);

        final String QUERY_CONFLICTS = """
            SELECT %s, %s
            FROM Programmation %s
            INNER JOIN Spectacle %s ON %s.spectacle_id = %s.id
            INNER JOIN Lieu %s ON %s.lieu_id = %s.id
            INNER JOIN SpectaclePersonnage sp1 ON sp1.spectacle_id = %s.id
            INNER JOIN SpectaclePersonnage sp2 ON sp2.spectacle_id = ?
            WHERE sp1.personnage_id = sp2.personnage_id
            AND %s.jour_semaine = ?
            AND %s.spectacle_id != ?
        """.formatted(
                Programmation.SELECT_COLUMNS, Spectacle.SELECT_COLUMNS,
                Programmation.ALIAS_NAME,
                Spectacle.ALIAS_NAME, Programmation.ALIAS_NAME, Spectacle.ALIAS_NAME,
                Lieu.ALIAS_NAME, Spectacle.ALIAS_NAME, Lieu.ALIAS_NAME,
                Spectacle.ALIAS_NAME,
                Programmation.ALIAS_NAME,
                Programmation.ALIAS_NAME
        );

        List<Spectacle> spectacleResult = DBRequest.executeSelect(
                CHECK_SPECTACLE,
                Utils.map(1, spectacleId),
                Spectacle::fromResultSet
        );
        if (spectacleResult == null || spectacleResult.isEmpty()) {
            throw new IllegalArgumentException("Spectacle introuvable");
        }
        int lieuId = spectacleResult.get(0).getLieu().getId();

        Integer jour = props.jourSemaine();
        if (jour == null || jour < 0 || jour > 6) {
            throw new IllegalArgumentException("Jour de la semaine invalide");
        }
        if (!props.heureFermeture().after(props.heureOuverture())) {
            throw new IllegalArgumentException(
                    "L'heure de fermeture doit être après l'heure d'ouverture"
            );
        }

        List<ProgrammationWithSpectacle> conflicts = DBRequest.executeSelect(
                QUERY_CONFLICTS,
                Utils.map(
                        1, spectacleId,
                        2, jour,
                        3, spectacleId
                ),
                ProgrammationWithSpectacle::fromResultSet
        );

        for (ProgrammationWithSpectacle conflict : conflicts) {
            Programmation programmation = conflict.programmation();
            Spectacle spectacle = conflict.spectacle();

            if (programmation.getJourSemaine().getValeur() != props.jourSemaine()) {
                continue;
            }

            if (ConflictChecker.heuresSeChevauche(props.heureOuverture(), props.heureFermeture(),
                    programmation.getHeureOuverture(), programmation.getHeureFermeture())) {
                throw new IllegalArgumentException(
                        "Conflit : personnage déjà programmé à ce créneau"
                );
            }

            if (lieuId != spectacle.getLieu().getId() &&
                    !ConflictChecker.respecteBattement(props.heureOuverture(), props.heureFermeture(),
                            programmation.getHeureOuverture(), programmation.getHeureFermeture())) {
                throw new IllegalArgumentException(
                        "Le personnage nécessite 30 min de battement entre spectacles"
                );
            }
        }

        final String QUERY_INSERT = """
                INSERT INTO Programmation (spectacle_id, jour_semaine, heure_debut, heure_fin)
                VALUES (?, ?, ?, ?)
            """;
        return DBRequest.executeCommand(
                QUERY_INSERT,
                Utils.map(
                        1, spectacleId,
                        2, props.jourSemaine(),
                        3, props.heureOuverture(),
                        4, props.heureFermeture()
                )
        );
    }

    public IdResponse setProgrammation(int spectacleId, List<ProgrammationProps> props) {
        for (int i = 0; i < props.size(); i++) {
            ProgrammationProps prog1 = props.get(i);
            if (!prog1.heureFermeture().after(prog1.heureOuverture())) {
                throw new IllegalArgumentException(
                        "L'heure de fermeture doit être après l'heure d'ouverture"
                );
            }

            for (int j = i + 1; j < props.size(); j++) {
                ProgrammationProps prog2 = props.get(j);

                if (prog1.jourSemaine().equals(prog2.jourSemaine())) {
                    if (ConflictChecker.heuresSeChevauche(
                            prog1.heureOuverture(), prog1.heureFermeture(),
                            prog2.heureOuverture(), prog2.heureFermeture()
                    )) {
                        throw new IllegalArgumentException(
                                "Conflit entre les créneaux demandés pour le même spectacle"
                        );
                    }
                }
            }
        }

        final String CHECK_SPECTACLE = """
            SELECT %s
            FROM Spectacle %s
            INNER JOIN Lieu %s ON %s.lieu_id = %s.id
            WHERE %s.id = ?
        """.formatted(Spectacle.SELECT_COLUMNS, Spectacle.ALIAS_NAME,
                Lieu.ALIAS_NAME, Spectacle.ALIAS_NAME, Lieu.ALIAS_NAME, Spectacle.ALIAS_NAME);

        List<Spectacle> spectacleResult = DBRequest.executeSelect(
                CHECK_SPECTACLE,
                Utils.map(1, spectacleId),
                Spectacle::fromResultSet
        );

        if (spectacleResult == null || spectacleResult.isEmpty()) {
            throw new IllegalArgumentException("Spectacle introuvable");
        }
        int lieuId = spectacleResult.get(0).getLieu().getId();

        if (props.isEmpty()) {
            final String QUERY_DELETE = """
                DELETE FROM Programmation WHERE spectacle_id = ?
            """;
            DBRequest.executeCommand(QUERY_DELETE, Utils.map(1, spectacleId));
            return new IdResponse(spectacleId);
        }

        List<Personnage> personnages = getPersonnagesFromSpectacle(spectacleId);

        for (Personnage personnage : personnages) {
            for (ProgrammationProps prog : props) {
                ConflictChecker.checkAllConflicts(
                        personnage.getId(),
                        prog.jourSemaine(),
                        prog.heureOuverture(),
                        prog.heureFermeture(),
                        lieuId,
                        null
                );
            }
        }

        final String QUERY_DELETE = """
            DELETE FROM Programmation WHERE spectacle_id = ?
        """;
        DBRequest.executeCommand(QUERY_DELETE, Utils.map(1, spectacleId));

        final String QUERY_INSERT = """
            INSERT INTO Programmation (spectacle_id, jour_semaine, heure_debut, heure_fin)
            VALUES (?, ?, ?, ?)
        """;

        for (ProgrammationProps prog : props) {
            DBRequest.executeCommand(
                    QUERY_INSERT,
                    Utils.map(
                            1, spectacleId,
                            2, prog.jourSemaine(),
                            3, prog.heureOuverture(),
                            4, prog.heureFermeture()
                    )
            );
        }

        return new IdResponse(spectacleId);
    }

    public IdResponse addPersonnage(int spectacleId, int personnageId) {
        final String CHECK_SPECTACLE = """
            SELECT %s
            FROM Spectacle %s
            INNER JOIN Lieu %s ON %s.lieu_id = %s.id
            WHERE %s.id = ?
        """.formatted(Spectacle.SELECT_COLUMNS, Spectacle.ALIAS_NAME,
                Lieu.ALIAS_NAME, Spectacle.ALIAS_NAME, Lieu.ALIAS_NAME, Spectacle.ALIAS_NAME);

        List<Spectacle> spectacleResult = DBRequest.executeSelect(
                CHECK_SPECTACLE,
                Utils.map(1, spectacleId),
                Spectacle::fromResultSet
        );
        if (spectacleResult == null || spectacleResult.isEmpty()) {
            throw new IllegalArgumentException("Spectacle introuvable");
        }

        final String CHECK_PERSONNAGE = """
            SELECT %s
            FROM Personnage %s
            WHERE %s.id = ?
        """.formatted(Personnage.SELECT_COLUMNS, Personnage.ALIAS_NAME, Personnage.ALIAS_NAME);

        List<Personnage> personnageResult = DBRequest.executeSelect(
                CHECK_PERSONNAGE,
                Utils.map(1, personnageId),
                Personnage::fromResultSet
        );
        if (personnageResult == null || personnageResult.isEmpty()) {
            throw new IllegalArgumentException("Personnage introuvable");
        }

        final String CHECK_EXISTS = """
            SELECT COUNT(*) as count
            FROM SpectaclePersonnage
            WHERE spectacle_id = ? AND personnage_id = ?
        """;

        List<Map<String, Object>> existsResult = DBRequest.executeSelect(
                CHECK_EXISTS,
                Utils.map(1, spectacleId, 2, personnageId),
                rs -> {
                    try {
                        return Map.of("count", rs.getInt("count"));
                    } catch (Exception e) {
                        return null;
                    }
                }
        );

        if (existsResult != null && !existsResult.isEmpty() &&
            (Integer) existsResult.get(0).get("count") > 0) {
            throw new IllegalArgumentException("Ce personnage est déjà associé à ce spectacle");
        }

        List<Programmation> spectacleProgrammations = getProgrammationFromSpectacle(spectacleId);
        int spectacleLieuId = spectacleResult.get(0).getLieu().getId();

        final String CHECK_SPECTACLE_CONFLICTS = """
            SELECT %s, %s
            FROM Programmation %s
            INNER JOIN Spectacle %s ON %s.spectacle_id = %s.id
            INNER JOIN Lieu %s ON %s.lieu_id = %s.id
            INNER JOIN SpectaclePersonnage sp ON sp.spectacle_id = %s.id
            WHERE sp.personnage_id = ?
        """.formatted(
                Programmation.SELECT_COLUMNS, Spectacle.SELECT_COLUMNS,
                Programmation.ALIAS_NAME,
                Spectacle.ALIAS_NAME, Programmation.ALIAS_NAME, Spectacle.ALIAS_NAME,
                Lieu.ALIAS_NAME, Spectacle.ALIAS_NAME, Lieu.ALIAS_NAME,
                Spectacle.ALIAS_NAME
        );

        List<ProgrammationWithSpectacle> existingSpectacles = DBRequest.executeSelect(
                CHECK_SPECTACLE_CONFLICTS,
                Utils.map(1, personnageId),
                ProgrammationWithSpectacle::fromResultSet
        );

        // Vérifier les conflits avec les spectacles
        for (Programmation prog : spectacleProgrammations) {
            for (ProgrammationWithSpectacle existing : existingSpectacles) {
                Programmation existingProg = existing.programmation();
                Spectacle existingSpectacle = existing.spectacle();

                if (prog.getJourSemaine().getValeur() != existingProg.getJourSemaine().getValeur()) {
                    continue;
                }

                if (ConflictChecker.heuresSeChevauche(prog.getHeureOuverture(), prog.getHeureFermeture(),
                        existingProg.getHeureOuverture(), existingProg.getHeureFermeture())) {
                    throw new IllegalArgumentException(
                            "Conflit : le personnage est déjà programmé dans le spectacle '"
                            + existingSpectacle.getTitre() + "' à ce créneau"
                    );
                }

                if (spectacleLieuId != existingSpectacle.getLieu().getId()) {
                    long minutesBetween = ConflictChecker.calculateMinutesBetween(
                        prog.getHeureOuverture(), prog.getHeureFermeture(),
                        existingProg.getHeureOuverture(), existingProg.getHeureFermeture()
                    );

                    if (minutesBetween < 30) {
                        throw new IllegalArgumentException(
                                "Le personnage nécessite 30 min de battement entre les spectacles '"
                                + existingSpectacle.getTitre() + "' et le nouveau spectacle (lieux différents). "
                                + "Temps disponible : " + minutesBetween + " min"
                        );
                    }
                }
            }
        }

        final String CHECK_RENCONTRE_CONFLICTS = """
            SELECT rp.jour_semaine, rp.heure_debut, rp.heure_fin, rp.lieu_id
            FROM RencontrePersonnage rp
            WHERE rp.personnage_id = ?
        """;

        List<Map<String, Object>> existingRencontres = DBRequest.executeSelect(
                CHECK_RENCONTRE_CONFLICTS,
                Utils.map(1, personnageId),
                rs -> {
                    try {
                        return Map.of(
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

        for (Programmation prog : spectacleProgrammations) {
            for (Map<String, Object> rencontre : existingRencontres) {
                int rencontreJour = (Integer) rencontre.get("jourSemaine");
                Time rencontreDebut = (Time) rencontre.get("heureDebut");
                Time rencontreFin = (Time) rencontre.get("heureFin");
                int rencontreLieuId = (Integer) rencontre.get("lieuId");

                if (prog.getJourSemaine().getValeur() != rencontreJour) {
                    continue;
                }

                if (ConflictChecker.heuresSeChevauche(prog.getHeureOuverture(), prog.getHeureFermeture(),
                        rencontreDebut, rencontreFin)) {
                    throw new IllegalArgumentException(
                            "Conflit : le personnage a déjà une rencontre prévue à ce créneau"
                    );
                }

                if (spectacleLieuId != rencontreLieuId) {
                    long minutesBetween = calculateMinutesBetween(
                        prog.getHeureOuverture(), prog.getHeureFermeture(),
                        rencontreDebut, rencontreFin
                    );

                    if (minutesBetween < 30) {
                        throw new IllegalArgumentException(
                                "Le personnage nécessite 30 min de battement entre sa rencontre "
                                + "et le spectacle (lieux différents). "
                                + "Temps disponible : " + minutesBetween + " min"
                        );
                    }
                }
            }
        }

        final String QUERY_INSERT = """
            INSERT INTO SpectaclePersonnage (spectacle_id, personnage_id)
            VALUES (?, ?)
        """;

        return DBRequest.executeCommand(
                QUERY_INSERT,
                Utils.map(1, spectacleId, 2, personnageId)
        );
    }

    private long calculateMinutesBetween(Time debut1, Time fin1, Time debut2, Time fin2) {
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

    // Méthodes privées encore nécessaires pour setProgrammation() et addPersonnage()
    private List<Personnage> getPersonnagesFromSpectacle(int spectacleId) {
        final String QUERY_STRING = """
        SELECT %s
        FROM Personnage %s
        INNER JOIN SpectaclePersonnage sp ON sp.personnage_id = %s.id
        WHERE sp.spectacle_id = ?
    """.formatted(
                Personnage.SELECT_COLUMNS,
                Personnage.ALIAS_NAME,
                Personnage.ALIAS_NAME
        );
        return DBRequest.executeSelect(QUERY_STRING, Utils.map(1, spectacleId), Personnage::fromResultSet);
    }

    private List<Programmation> getProgrammationFromSpectacle(int spectacleId) {
        final String QUERY_STRING = """
                    SELECT %s
                    FROM Programmation %s
                    WHERE %s.spectacle_id = ?
                    ORDER BY %s.jour_semaine ASC
                """.formatted(
                Programmation.SELECT_COLUMNS,
                Programmation.ALIAS_NAME,
                Programmation.ALIAS_NAME,
                Programmation.ALIAS_NAME
        );
        return DBRequest.executeSelect(QUERY_STRING, Utils.map(1, spectacleId), Programmation::fromResultSet);
    }

    public IdResponse removePersonnage(int spectacleId, int personnageId) {
        final String CHECK_EXISTS = """
            SELECT COUNT(*) as count
            FROM SpectaclePersonnage
            WHERE spectacle_id = ? AND personnage_id = ?
        """;

        List<Map<String, Object>> existsResult = DBRequest.executeSelect(
                CHECK_EXISTS,
                Utils.map(1, spectacleId, 2, personnageId),
                rs -> {
                    try {
                        return Map.of("count", rs.getInt("count"));
                    } catch (Exception e) {
                        return null;
                    }
                }
        );

        if (existsResult == null || existsResult.isEmpty() ||
            (Integer) existsResult.get(0).get("count") == 0) {
            throw new IllegalArgumentException("Ce personnage n'est pas associé à ce spectacle");
        }

        final String QUERY_DELETE = """
            DELETE FROM SpectaclePersonnage
            WHERE spectacle_id = ? AND personnage_id = ?
        """;

        return DBRequest.executeCommand(
                QUERY_DELETE,
                Utils.map(1, spectacleId, 2, personnageId)
        );
    }
}
