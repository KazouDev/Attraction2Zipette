package com.b2z.model;

import com.b2z.utils.Horaire;
import com.b2z.utils.JourSemaine;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

public class HoraireOuverture extends Horaire {
    public HoraireOuverture(JourSemaine jourSemaine, Time heureOuverture, Time heureFermeture) {
       super(jourSemaine, heureOuverture, heureFermeture);
    }

    public static HoraireOuverture fromResultSet(ResultSet rs) {
        try {

            int jour = rs.getInt("jour_semaine");
            if (jour < 0 || jour > 6) {
                return null;
            }
            JourSemaine jourSemaine = JourSemaine.fromInt(jour);
            Time ouverture = rs.getTime("heure_ouverture");
            Time fermeture = rs.getTime("heure_fermeture");
            return new HoraireOuverture(jourSemaine, ouverture, fermeture);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}

