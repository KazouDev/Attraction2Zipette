package com.b2z.controller;

import com.b2z.dao.IdResponse;
import com.b2z.dao.RencontreDAO;
import com.b2z.model.RencontrePersonnage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/rencontres")
public class RencontreRessource {

    private final RencontreDAO rencontreDAO = new RencontreDAO();


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<RencontrePersonnage> getRecontres(){
        return rencontreDAO.findAll();
    }

    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public RencontrePersonnage getRencontreById(@PathParam("id") @NotNull int id){
        return rencontreDAO.findById(id);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public IdResponse createRencontre(@Valid @NotNull RencontreDAO.RencontreProps rencontre) {
        return rencontreDAO.create(rencontre);
    }

    @Path("/{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public IdResponse deleteRencontre(@PathParam("id") @NotNull int id) {
        return rencontreDAO.delete(id);
    }
}
