package com.b2z.utils;

public enum JourSemaine {
    LUNDI(0),
    MARDI(1),
    MERCREDI(2),
    JEUDI(3),
    VENDREDI(4),
    SAMEDI(5),
    DIMANCHE(6);

    private final int valeur;

    JourSemaine(int i) {
        this.valeur = i;
    }

    public int getValeur() {
        return valeur;
    }

    public static JourSemaine fromInt(int i) {
        for (JourSemaine jour : JourSemaine.values()) {
            if (jour.getValeur() == i) {
                return jour;
            }
        }
        throw new IllegalArgumentException("Valeur invalide pour JourSemaine: " + i);
    }
}
