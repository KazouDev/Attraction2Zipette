package com.b2z.controller;

import com.b2z.dao.AttractionDAO;
import com.b2z.model.Attraction;
import com.google.gson.Gson;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.Map;

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

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Attraction create(@Valid AttractionDAO.AttractionProps attraction) {
        return attractionDAO.create(attraction);
    }

    // FIXME update
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String update(@PathParam("id") @NotNull int id, String body) {
        return null;
        //Gson gson = new Gson();
        //Attraction updatedData = gson.fromJson(body, Attraction.class);
        //Attraction updated = attractionDAO.update(id, updatedData);
        //return gson.toJson(updated);
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteById(@PathParam("id") @NotNull int id) {
        Attraction attraction = attractionDAO.delete(id);
        Gson gson = new Gson();
        return gson.toJson(attraction);
    }
}