package com.b2z.model;

import com.b2z.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Spectacle {
    private int id;
    private String titre;
    private List<Personnage> personnages = new ArrayList<>();
    private List<Programmation> programmations = new ArrayList<>();
    private Lieu lieu;

    public static final String ALIAS_NAME = "spectacle";
    public static final String SELECT_COLUMNS = Utils.prefixColumns(
            "spectacle", "spectacle",
            "id", "titre"
    ) + ", " + Lieu.SELECT_COLUMNS;

    public Spectacle(int id, String titre, Lieu lieu) {
        this.id = id;
        this.titre = titre;
        this.lieu = lieu;
    }

    public static Spectacle fromResultSet(java.sql.ResultSet rs) {
        try {
            int id = rs.getInt("spectacle_id");
            String titre = rs.getString("spectacle_titre");

            return new Spectacle(id, titre, Lieu.fromResultSet(rs));
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Lieu getLieu() {
        return lieu;
    }

    public void setLieu(Lieu lieu) {
        this.lieu = lieu;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public List<Personnage> getPersonnages() {
        return personnages;
    }

    public void setPersonnages(List<Personnage> personnages) {
        this.personnages = personnages;
    }

    public List<Programmation> getProgrammations() {
        return programmations;
    }

    public void setProgrammations(List<Programmation> programmations) {
        this.programmations = programmations;
    }
}

