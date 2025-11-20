package com.b2z.model;

import com.b2z.utils.JourSemaine;

import java.sql.Time;

public class RencontrePersonnage {
    private int id;
    private Personnage personnage;
    private Lieu lieu;
    private JourSemaine jourSemaine;
    private Time heureDebut;
    private Time heureFin;

    public static final String ALIAS_NAME = "rencontre_personnage";
    public static final String SELECT_COLUMNS = String.join(", ",
            ALIAS_NAME + ".id AS " + ALIAS_NAME + "_id",
            ALIAS_NAME + ".personnage_id AS " + ALIAS_NAME + "_personnage_id",
            ALIAS_NAME + ".lieu_id AS " + ALIAS_NAME + "_lieu_id",
            ALIAS_NAME + ".jour_semaine AS " + ALIAS_NAME + "_jour_semaine",
            ALIAS_NAME + ".heure_debut AS " + ALIAS_NAME + "_heure_debut",
            ALIAS_NAME + ".heure_fin AS " + ALIAS_NAME + "_heure_fin"
    );

    public RencontrePersonnage() {
    }

    public RencontrePersonnage(int id, Personnage personnage, Lieu lieu, JourSemaine jourSemaine, Time heureDebut, Time heureFin) {
        this.id = id;
        this.personnage = personnage;
        this.lieu = lieu;
        this.jourSemaine = jourSemaine;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
    }

    public static RencontrePersonnage fromResultSet(java.sql.ResultSet rs) {
        try {
            int id = rs.getInt("rencontre_personnage_id");
            Lieu lieu = Lieu.fromResultSet(rs);
            Personnage personnage = Personnage.fromResultSet(rs);
            int jour = rs.getInt("rencontre_personnage_jour_semaine");
            if (jour < 0 || jour > 6) {
                return null;
            }
            JourSemaine jourSemaine = JourSemaine.fromInt(jour);
            Time heureDebut = rs.getTime("rencontre_personnage_heure_debut");
            Time heureFin = rs.getTime("rencontre_personnage_heure_fin");
            return new RencontrePersonnage(id, personnage, lieu, jourSemaine,  heureDebut, heureFin);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getSelectColumns() {
        return SELECT_COLUMNS + ", " + Lieu.SELECT_COLUMNS + ", " + Personnage.SELECT_COLUMNS;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Personnage getPersonnage() {
        return personnage;
    }

    public void setPersonnage(Personnage personnage) {
        this.personnage = personnage;
    }

    public Lieu getLieu() {
        return lieu;
    }

    public void setLieu(Lieu lieu) {
        this.lieu = lieu;
    }

    public JourSemaine getJourSemaine() {
        return jourSemaine;
    }

    public void setJourSemaine(JourSemaine jourSemaine) {
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
}

