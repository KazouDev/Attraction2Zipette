package com.b2z.model;

import com.b2z.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public void addProgrammationIfNotExists(Programmation programmation) {
        if (programmation == null) return;

        boolean exists = programmations.stream().anyMatch(p ->
                p.getJourSemaine().equals(programmation.getJourSemaine()) &&
                        p.getHeureOuverture().equals(programmation.getHeureOuverture()) &&
                        p.getHeureFermeture().equals(programmation.getHeureFermeture())
        );

        if (!exists) {
            programmations.add(programmation);
        }
    }

    public void addPersonnageIfNotExists(Personnage personnage) {
        if (personnage == null) return;

        boolean exists = personnages.stream().anyMatch(p -> p.getId() == personnage.getId());

        if (!exists) {
            personnages.add(personnage);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Spectacle spectacle = (Spectacle) o;
        return id == spectacle.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
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
