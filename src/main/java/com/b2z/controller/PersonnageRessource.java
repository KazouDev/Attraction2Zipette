package com.b2z.controller;

import com.b2z.dao.IdResponse;
import com.b2z.dao.PersonnageDAO;
import com.b2z.model.Personnage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.Map;

@Path("/personnages")
public class PersonnageRessource {

    private final PersonnageDAO personnageDAO = new PersonnageDAO();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Personnage> getAllPersonnages() {
        return personnageDAO.findAll();
    }

    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Personnage getPersonnageById(@PathParam("id") @NotNull int id) {
        return personnageDAO.findById(id);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public IdResponse createPersonnage(@Valid @NotNull PersonnageDAO.PersonnageProps personnage) {
        return personnageDAO.create(personnage);
    }

    @Path("/{id}")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public IdResponse updatePersonnage(
            @PathParam("id") @NotNull int id,
            @Valid @NotNull Map<String, Object> updates
    ) {
        return personnageDAO.update(id, updates);
    }

    @Path("/{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public IdResponse deletePersonnage(@PathParam("id") @NotNull int id) {
        return personnageDAO.delete(id);
    }
}

