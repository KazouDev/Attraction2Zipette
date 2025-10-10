package com.b2z.controller;

import com.b2z.dao.AttractionDAO;
import com.b2z.model.Attraction;import com.google.gson.Gson;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/attractions")
public class AttractionResource {

    private final AttractionDAO attractionDAO = new AttractionDAO();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getAll() {
        List<Attraction> attractions = attractionDAO.findAll();
        Gson gson = new Gson();
        return gson.toJson(attractions);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getById(@PathParam("id") @NotNull int id) {
        Attraction attraction = attractionDAO.findById(id);
        if (attraction == null) {
            throw new WebApplicationException("Attraction not found", 404);
        }
        Gson gson = new Gson();
        return gson.toJson(attraction);
    }

    // create

    // update

    // delete
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteById(@PathParam("id") @NotNull int id) {
        Attraction attraction = attractionDAO.delete(id);
        Gson gson = new Gson();
        return gson.toJson(attraction);
    }
}