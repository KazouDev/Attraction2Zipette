package com.b2z.model;

import com.b2z.service.ThemeParkAPI;
import com.b2z.utils.Utils;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Attraction {

    public static final String ALIAS_NAME = "attraction";
    private static final String SELECT_COLUMNS = Utils.prefixColumns(
            "attraction",
            "attraction",
            "id", "nom", "taille_min", "taille_min_adulte"
    );

    private int id;

    private String nom;

    private AttractionType type;

    private int tailleMin;

    private int tailleMinAdulte;

    private List<HoraireOuverture> horaireOuvertures;
    private Integer tempsAttente;

    public Attraction(int id, String nom, AttractionType type, int tailleMin, int tailleMinAdulte) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.tailleMin = tailleMin;
        this.tailleMinAdulte = tailleMinAdulte;
        this.horaireOuvertures = new ArrayList<>();

        final Map<String, ThemeParkAPI.AttractionCrowd> crowds = ThemeParkAPI.getInstance().getCrowds();
        ThemeParkAPI.AttractionCrowd data = crowds.get(Integer.toString(this.id));
        this.tempsAttente = data != null ? data.waitDuration() : 0;
    }

    public static String getSelectColumns() {
        return Attraction.SELECT_COLUMNS + ", " + AttractionType.SELECT_COLUMNS;
    }


    public static Attraction fromResultSet(ResultSet rs) {
        try {
            int id = rs.getInt(ALIAS_NAME + "_id");
            String nom = rs.getString(ALIAS_NAME + "_nom");
            AttractionType type = AttractionType.fromResultSet(rs);
            int tailleMin = rs.getInt(ALIAS_NAME + "_taille_min");
            int tailleMinAdulte = rs.getInt(ALIAS_NAME + "_taille_min_adulte");
            return new Attraction(id, nom, type, tailleMin, tailleMinAdulte);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getTempsAttente() {
        return this.tempsAttente;
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

    public void setTempsAttente(Integer tempsAttente) {
        this.tempsAttente = tempsAttente;
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

