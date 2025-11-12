package com.b2z.model;

import java.sql.SQLException;

public class PersonnageWithActivity {
    private Personnage personnage;
    private long total_duration; // in minutes

    private PersonnageWithActivity(Personnage personnage, long total_duration) {
        this.personnage = personnage;
        this.total_duration = total_duration;
    }

    public static PersonnageWithActivity fromResultSet(java.sql.ResultSet rs) {
        try {
            Personnage personnage = Personnage.fromResultSet(rs);
            long duration = rs.getLong("total_activity");
            if (personnage != null) {
                return new PersonnageWithActivity(personnage, duration);
            } else {
                return null;
            }
        } catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public Personnage getPersonnage() {
        return personnage;
    }

    public long getTotalActivityDuration() {
        return total_duration;
    }
}
