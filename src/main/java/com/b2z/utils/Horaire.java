package com.b2z.utils;

import com.b2z.utils.JourSemaine;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

public abstract class Horaire {

    private JourSemaine jourSemaine;
    private Time heureOuverture;
    private Time heureFermeture;

    public Horaire(JourSemaine jourSemaine, Time heureOuverture, Time heureFermeture) {
        this.jourSemaine = jourSemaine;
        this.heureOuverture = heureOuverture;
        this.heureFermeture = heureFermeture;
    }

    public JourSemaine getJourSemaine() {
        return this.jourSemaine;
    }

    public void setJourSemaine(JourSemaine jourSemaine) {
        this.jourSemaine = jourSemaine;
    }

    public Time getHeureOuverture() {
        return heureOuverture;
    }

    public void setHeureOuverture(Time heureOuverture) {
        this.heureOuverture = heureOuverture;
    }

    public Time getHeureFermeture() {
        return heureFermeture;
    }

    public void setHeureFermeture(Time heureFermeture) {
        this.heureFermeture = heureFermeture;
    }

}
