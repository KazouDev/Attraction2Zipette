package com.b2z.model;

import com.b2z.utils.Horaire;
import com.b2z.utils.JourSemaine;
import com.b2z.utils.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

public class Programmation extends Horaire {
    public static String ALIAS_NAME = "programmation";
    public static final String SELECT_COLUMNS = Utils.prefixColumns(ALIAS_NAME, ALIAS_NAME, "jour_semaine", "heure_debut", "heure_fin");

    public Programmation(JourSemaine jourSemaine, Time heureDebut, Time heureFin) {
        super(jourSemaine, heureDebut, heureFin);
    }

    public static Programmation fromResultSet(ResultSet rs) {
        try {

            int jour = rs.getInt("programmation_jour_semaine");
            if (jour < 0 || jour > 6) {
                return null;
            }
            JourSemaine jourSemaine = JourSemaine.fromInt(jour);
            Time ouverture = rs.getTime("programmation_heure_debut");
            Time fermeture = rs.getTime("programmation_heure_fin");
            return new Programmation(jourSemaine, ouverture, fermeture);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}

