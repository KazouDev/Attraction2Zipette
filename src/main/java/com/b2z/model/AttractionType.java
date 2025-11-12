package com.b2z.model;

import com.b2z.utils.Utils;

import java.sql.ResultSet;

public class AttractionType {
    public static final String ALIAS_NAME = "attraction_type";
    public static final String SELECT_COLUMNS = Utils.prefixColumns(
            "attraction_type",
            "attraction_type",
            "id", "nom"
    );

    private int id;
    private String nom;

    public AttractionType(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public static AttractionType fromResultSet(ResultSet rs) {
        try {
            int id = rs.getInt("attraction_type_id");
            String nom = rs.getString("attraction_type_nom");
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

