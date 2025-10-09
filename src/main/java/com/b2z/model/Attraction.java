package com.b2z.model;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Attraction {
    private int id;
    private String nom;
    private AttractionType type;
    private int tailleMin;
    private int tailleMinAdulte;

    private List<HoraireOuverture> horaireOuvertures = new ArrayList<>();

    public Attraction(int id, String nom, AttractionType type, int tailleMin, int tailleMinAdulte) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.tailleMin = tailleMin;
        this.tailleMinAdulte = tailleMinAdulte;
    }

    public static Attraction fromResultSet(ResultSet rs) {
        try {
            int id = rs.getInt("id");
            String nom = rs.getString("nom");
            AttractionType type = AttractionType.fromResultSet(rs);
            int tailleMin = rs.getInt("taille_min");
            int tailleMinAdulte = rs.getInt("taille_min_adulte");
            return new Attraction(id, nom, type, tailleMin, tailleMinAdulte);
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

    public AttractionType getType() {
        return this.type;
    }

    public void setType(AttractionType type) {
        this.type = type;
    }

    public int getTailleMin() {
        return tailleMin;
    }

    public void setTailleMin(int tailleMin) {
        this.tailleMin = tailleMin;
    }

    public int getTailleMinAdulte() {
        return tailleMinAdulte;
    }

    public void setTailleMinAdulte(int tailleMinAdulte) {
        this.tailleMinAdulte = tailleMinAdulte;
    }

    public List<HoraireOuverture> getHoraireOuvertures() {
        return horaireOuvertures;
    }

    public void setHoraireOuvertures(List<HoraireOuverture> horaireOuvertures) {
        this.horaireOuvertures = horaireOuvertures;
    }

    public void addHoraireOuverture(HoraireOuverture horaireOuverture) {
        this.horaireOuvertures.add(horaireOuverture);
    }


}

