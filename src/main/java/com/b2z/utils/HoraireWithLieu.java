package com.b2z.utils;

import com.b2z.model.Lieu;

import java.sql.Time;

public class HoraireWithLieu extends Horaire {
    private Lieu lieu;
    public HoraireWithLieu(JourSemaine jourSemaine, Time heureOuverture, Time heureFermeture, Lieu lieu) {
        super(jourSemaine, heureOuverture, heureFermeture);
        this.lieu = lieu;
    }

    public Lieu getLieu() {
        return lieu;
    }
}
