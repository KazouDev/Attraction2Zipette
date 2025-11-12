package com.b2z.dao;

import com.b2z.model.Lieu;
import com.b2z.model.Personnage;
import com.b2z.model.RencontrePersonnage;
import com.b2z.utils.ConflictChecker;
import com.b2z.utils.DBRequest;
import com.b2z.utils.Utils;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.sql.Time;
import java.util.List;
import java.util.Map;

public class RencontreDAO implements DAOInterface {

    public record RencontreProps(
            @NotNull int personnageId,
            @NotNull int lieuId,
            @NotNull @Min(0) @Max(6) Integer jourSemaine,
            @NotNull Time heureOuverture,
            @NotNull Time heureFermeture
    ) {}

    @Override
    public List<RencontrePersonnage> findAll() {
        final String QUERY_STRING = """
            SELECT %s
            FROM RencontrePersonnage %s
            INNER JOIN Personnage %s ON %s.personnage_id = %s.id
            INNER JOIN Lieu %s ON %s.lieu_id = %s.id
            ORDER BY %s.jour_semaine ASC, %s.heure_debut ASC
        """.formatted(
            RencontrePersonnage.getSelectColumns(),
            RencontrePersonnage.ALIAS_NAME,
                Personnage.ALIAS_NAME,
            RencontrePersonnage.ALIAS_NAME,
            Personnage.ALIAS_NAME,
                Lieu.ALIAS_NAME,
            RencontrePersonnage.ALIAS_NAME,
            Lieu.ALIAS_NAME,
            RencontrePersonnage.ALIAS_NAME,
            RencontrePersonnage.ALIAS_NAME
        );
        return DBRequest.executeSelect(QUERY_STRING, null, RencontrePersonnage::fromResultSet);
    }

    @Override
    public RencontrePersonnage findById(int id) {
        final String QUERY_STRING = """
            SELECT %s
            FROM RencontrePersonnage %s
            INNER JOIN Personnage %s ON %s.personnage_id = %s.id
            INNER JOIN Lieu %s ON %s.lieu_id = %s.id
            WHERE %s.id = ?
        """.formatted(
                RencontrePersonnage.getSelectColumns(),
                RencontrePersonnage.ALIAS_NAME,
                Personnage.ALIAS_NAME,
                RencontrePersonnage.ALIAS_NAME,
                Personnage.ALIAS_NAME,
                Lieu.ALIAS_NAME,
                RencontrePersonnage.ALIAS_NAME,
                Lieu.ALIAS_NAME,
                RencontrePersonnage.ALIAS_NAME
        );
        List<RencontrePersonnage> result = DBRequest.executeSelect(QUERY_STRING, Utils.map(1, id), RencontrePersonnage::fromResultSet);
        if (result == null || result.isEmpty()) {
            throw new IllegalArgumentException("Rencontre introuvable");
        }

        return result.get(0);
    }

    @Override
    public IdResponse create(Object propsObj) {
        if (!(propsObj instanceof RencontreProps)) {
            throw new IllegalArgumentException("Props invalides");
        }

        RencontreProps props = (RencontreProps) propsObj;

        // Vérifier que le personnage existe
        final String CHECK_PERSONNAGE = """
            SELECT %s
            FROM Personnage %s
            WHERE %s.id = ?
        """.formatted(Personnage.SELECT_COLUMNS, Personnage.ALIAS_NAME, Personnage.ALIAS_NAME);

        List<Personnage> personnageResult = DBRequest.executeSelect(
                CHECK_PERSONNAGE,
                Utils.map(1, props.personnageId()),
                Personnage::fromResultSet
        );
        if (personnageResult == null || personnageResult.isEmpty()) {
            throw new IllegalArgumentException("Personnage introuvable");
        }

        // Vérifier que le lieu existe
        final String CHECK_LIEU = """
            SELECT %s
            FROM Lieu %s
            WHERE %s.id = ?
        """.formatted(Lieu.SELECT_COLUMNS, Lieu.ALIAS_NAME, Lieu.ALIAS_NAME);

        List<Lieu> lieuResult = DBRequest.executeSelect(
                CHECK_LIEU,
                Utils.map(1, props.lieuId()),
                Lieu::fromResultSet
        );
        if (lieuResult == null || lieuResult.isEmpty()) {
            throw new IllegalArgumentException("Lieu introuvable");
        }

        // Vérifier la cohérence des horaires
        if (!props.heureFermeture().after(props.heureOuverture())) {
            throw new IllegalArgumentException(
                    "L'heure de fin doit être après l'heure de début"
            );
        }

        // Vérifier tous les conflits (spectacles + rencontres) via ConflictChecker
        ConflictChecker.checkAllConflicts(
                props.personnageId(),
                props.jourSemaine(),
                props.heureOuverture(),
                props.heureFermeture(),
                props.lieuId(),
                null
        );

        final String QUERY_STRING = """
            INSERT INTO RencontrePersonnage (personnage_id, lieu_id, jour_semaine, heure_debut, heure_fin)
            VALUES (?, ?, ?, ?, ?)
        """;

        IdResponse result = DBRequest.executeCommand(
                QUERY_STRING,
                Utils.map(
                        1, props.personnageId(),
                        2, props.lieuId(),
                        3, props.jourSemaine(),
                        4, props.heureOuverture(),
                        5, props.heureFermeture()
                )
        );

        if (result == null) {
            throw new IllegalArgumentException("Erreur lors de la création de la rencontre");
        }

        return result;
    }

    @Override
    public IdResponse delete(int id) {
        // Vérifier que la rencontre existe
        findById(id);

        final String QUERY_STRING = """
            DELETE FROM RencontrePersonnage
            WHERE id = ?
        """;

        IdResponse result = DBRequest.executeCommand(QUERY_STRING, Utils.map(1, id));
        if (result == null) {
            throw new IllegalArgumentException("Erreur lors de la suppression de la rencontre");
        }

        return result;
    }
}
