package com.b2z.model;

import com.b2z.utils.Horaire;
import com.b2z.utils.JourSemaine;
import com.b2z.utils.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

public class HoraireOuverture extends Horaire {
    public static String ALIAS_NAME = "horaire_ouverture";
    public static final String SELECT_COLUMNS = Utils.prefixColumns(
            "horaire_ouverture",
            "horaire_ouverture",
            "jour_semaine", "heure_ouverture", "heure_fermeture"
    );

    public HoraireOuverture(JourSemaine jourSemaine, Time heureOuverture, Time heureFermeture) {
       super(jourSemaine, heureOuverture, heureFermeture);
    }

    public static HoraireOuverture fromResultSet(ResultSet rs) {
        try {

            int jour = rs.getInt("horaire_ouverture_jour_semaine");
            if (jour < 0 || jour > 6) {
                return null;
            }
            JourSemaine jourSemaine = JourSemaine.fromInt(jour);
            Time ouverture = rs.getTime("horaire_ouverture_heure_ouverture");
            Time fermeture = rs.getTime("horaire_ouverture_heure_fermeture");
            return new HoraireOuverture(jourSemaine, ouverture, fermeture);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}

