package com.b2z.model;

import com.b2z.utils.Horaire;
import com.b2z.utils.JourSemaine;

import java.sql.Time;

public class HoraireOuverture extends Horaire {
    public HoraireOuverture(JourSemaine jourSemaine, Time heureOuverture, Time heureFermeture) {
       super(jourSemaine, heureOuverture, heureFermeture);
    }
}

