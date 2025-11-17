package com.b2z.model;

import com.b2z.service.ThemeParkAPI;
import com.b2z.utils.Utils;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    // HashSet pour dédoublonnage O(1) des horaires
    private final Set<String> horaireKeys = new HashSet<>();

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

    /**
     * Ajoute un horaire si il n'existe pas déjà (évite les doublons)
     * Complexité: O(1) grâce au HashSet
     */
    public void addHoraireOuvertureIfNotPresent(HoraireOuverture horaire) {
        if (horaire == null) return;

        // Créer une clé unique pour l'horaire (jour + heures)
        String key = horaire.getJourSemaine().getValeur() + "_" +
                horaire.getHeureOuverture().toString() + "_" +
                horaire.getHeureFermeture().toString();

        // Vérification O(1) au lieu de O(n)
        if (horaireKeys.add(key)) {
            horaireOuvertures.add(horaire);
        }
    }

    public void addHoraireOuverture(HoraireOuverture horaireOuverture) {
        addHoraireOuvertureIfNotPresent(horaireOuverture);
    }

    public List<HoraireOuverture> getHoraireOuvertures() {
        return horaireOuvertures;
    }

    public void setHoraireOuvertures(List<HoraireOuverture> horaireOuvertures) {
        this.horaireOuvertures.clear();
        this.horaireKeys.clear();
        for (HoraireOuverture h : horaireOuvertures) {
            addHoraireOuvertureIfNotPresent(h);
        }
    }


}
