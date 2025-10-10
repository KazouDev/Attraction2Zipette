package com.b2z.model;

public class Personnage {
    private int id;
    private String nom;

    public Personnage(int id, String nom, String type, String description) {
        this.id = id;
        this.nom = nom;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}

