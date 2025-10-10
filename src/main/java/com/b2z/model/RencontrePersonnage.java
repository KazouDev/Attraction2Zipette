package com.b2z.model;

import java.sql.Time;

public class RencontrePersonnage {
    private int id;
    private int personnageId;
    private String personnageNom; // Pour l'affichage
    private int lieuId;
    private String lieuNom; // Pour l'affichage
    private String jourSemaine;
    private Time heureDebut;
    private Time heureFin;
    private int duree;

    public RencontrePersonnage() {
    }

    public RencontrePersonnage(int id, int personnageId, int lieuId, String jourSemaine, Time heureDebut, Time heureFin, int duree) {
        this.id = id;
        this.personnageId = personnageId;
        this.lieuId = lieuId;
        this.jourSemaine = jourSemaine;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.duree = duree;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPersonnageId() {
        return personnageId;
    }

    public void setPersonnageId(int personnageId) {
        this.personnageId = personnageId;
    }

    public String getPersonnageNom() {
        return personnageNom;
    }

    public void setPersonnageNom(String personnageNom) {
        this.personnageNom = personnageNom;
    }

    public int getLieuId() {
        return lieuId;
    }

    public void setLieuId(int lieuId) {
        this.lieuId = lieuId;
    }

    public String getLieuNom() {
        return lieuNom;
    }

    public void setLieuNom(String lieuNom) {
        this.lieuNom = lieuNom;
    }

    public String getJourSemaine() {
        return jourSemaine;
    }

    public void setJourSemaine(String jourSemaine) {
        this.jourSemaine = jourSemaine;
    }

    public Time getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(Time heureDebut) {
        this.heureDebut = heureDebut;
    }

    public Time getHeureFin() {
        return heureFin;
    }

    public void setHeureFin(Time heureFin) {
        this.heureFin = heureFin;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }
}

