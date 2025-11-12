package com.b2z.model;

import com.b2z.utils.Utils;

import java.sql.ResultSet;

public class Personnage {

    public static final String ALIAS_NAME = "personnage";
    public static final String SELECT_COLUMNS = Utils.prefixColumns(ALIAS_NAME, ALIAS_NAME, "id", "nom");

    private int id;
    private String nom;

    public static Personnage fromResultSet(ResultSet rs){
        try {
            int id = rs.getInt("personnage_id");
            String nom = rs.getString("personnage_nom");
            return new Personnage(id, nom);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public Personnage(int id, String nom) {
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

