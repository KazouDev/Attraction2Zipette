package com.b2z.controller;

import com.b2z.dao.StatistiqueDAO;
import com.b2z.dao.StatistiqueDAO.SpectacleWithDuration;
import com.b2z.model.PersonnageWithActivity;
import jakarta.ws.rs.*;

import java.util.List;

@Path("/stats")
public class StatstiqueRessource {

    private StatistiqueDAO statistiqueDAO = new StatistiqueDAO();

    @GET
    @Path("/top-personnages")
    @Produces("application/json")
    public List<PersonnageWithActivity> getTopPersonnages(@DefaultValue("5") @QueryParam("limit") int limit) {
        return statistiqueDAO.getTopPersonnagesActivity(limit);
    }

    @GET
    @Path("/spectacles")
    @Produces("application/json")
    public List<SpectacleWithDuration> getSpectaclesRanking(@QueryParam("day") Integer day) {
        if (day == null) {
            return statistiqueDAO.getSpectaclesRankingByDurationAllDays();
        }
        return statistiqueDAO.getSpectaclesRankingByDurationForDay(day);
    }

}
