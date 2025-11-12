package com.b2z.controller;

import com.b2z.dao.AttractionDAO;
import com.b2z.dao.IdResponse;
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
public class AttractionRessource {

    private final AttractionDAO attractionDAO = new AttractionDAO();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Attraction> getAll() {
        return attractionDAO.findAll();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Attraction getById(@PathParam("id") @NotNull int id) {
        Attraction attraction = attractionDAO.findById(id);
        if (attraction == null) {
            throw new WebApplicationException("Attraction not found", 404);
        }
        return attraction;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IdResponse create(@Valid AttractionDAO.AttractionProps attraction) {
        return attractionDAO.create(attraction);
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IdResponse update(@PathParam("id") @NotNull int id, @NotNull Map<String, Object> partialData) {
        return attractionDAO.updatePartial(id, partialData);
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public IdResponse deleteById(@PathParam("id") @NotNull int id) {
        return attractionDAO.delete(id);
    }

    @PUT
    @Path("/horaires/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IdResponse updateHoraireOuverture(@PathParam("id") @NotNull int id, @Valid List<AttractionDAO.HoraireOuvertureProps> horaires) {
        return attractionDAO.updateHoraireOuverture(id, horaires);
    }
}