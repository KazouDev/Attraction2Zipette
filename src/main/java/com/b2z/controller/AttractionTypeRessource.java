package com.b2z.controller;

import com.b2z.dao.IdResponse;
import com.b2z.dao.AttractionTypeDAO;
import com.b2z.model.AttractionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.Map;

@Path("/types")
public class AttractionTypeRessource {
    private final AttractionTypeDAO attractionTypeDAO = new AttractionTypeDAO();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<AttractionType> getAll() {
        return attractionTypeDAO.findAll();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AttractionType getById(@PathParam("id") @NotNull int id) {
        return attractionTypeDAO.findById(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IdResponse create(@Valid AttractionTypeDAO.AttractionTypeProps type) {
        return attractionTypeDAO.create(type);
    }

    @Path("/{id}")
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IdResponse delete(@PathParam("id") @NotNull int id){
        return attractionTypeDAO.delete(id);
    }

    @Path("/{id}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IdResponse updatePartial(@PathParam("id") @NotNull int id, @NotNull Map<String, Object> partial) {
        return attractionTypeDAO.update(id, partial);
    }
}
