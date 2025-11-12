package com.b2z.model;

public class Lieu {
    public static final String ALIAS_NAME = "lieu";
    public static final String SELECT_COLUMNS = "lieu.id AS lieu_id, lieu.nom AS lieu_nom";

    private int id;
    private String nom;

    public Lieu(int id, String nom) {
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

    public static Lieu fromResultSet(java.sql.ResultSet rs) {
        try {
            int id = rs.getInt("lieu_id");
            String nom = rs.getString("lieu_nom");
            return new Lieu(id, nom);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}

