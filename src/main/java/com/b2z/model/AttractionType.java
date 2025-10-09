package com.b2z.model;

import java.sql.ResultSet;

public class AttractionType {
    private int id;
    private String nom;


    public AttractionType(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public static AttractionType fromResultSet(ResultSet rs) {
        try {
            int id = rs.getInt("id");
            String nom = rs.getString("nom");
            return new AttractionType(id, nom);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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

