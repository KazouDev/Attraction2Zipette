package com.b2z.model;

import com.b2z.utils.Horaire;
import com.b2z.utils.JourSemaine;

import java.sql.Time;

public class Programmation extends Horaire {
    private int id;
    private Lieu lieu;

    public Programmation(JourSemaine jourSemaine, Time heureDebut, Time heureFin, Lieu lieu) {
        super(jourSemaine, heureDebut, heureFin);
        this.lieu = lieu;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Lieu getLieu() {
        return lieu;
    }

    public void setLieu(Lieu lieu) {
        this.lieu = lieu;
    }
}

