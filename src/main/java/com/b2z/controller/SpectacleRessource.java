package com.b2z.controller;

import com.b2z.dao.IdResponse;
import com.b2z.dao.SpectacleDAO;
import com.b2z.model.Spectacle;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.Map;

@Path("/spectacles")
public class SpectacleRessource {

    public SpectacleDAO spectacleDAO = new SpectacleDAO();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Spectacle> getSpectacles() {
        return spectacleDAO.findAll();
    }

    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Spectacle getSpectacleById(@PathParam("id") @NotNull int id) {
        return spectacleDAO.findById(id);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public IdResponse createSpectacle(@Valid @NotNull SpectacleDAO.SpectacleProps spectacle) {
        return spectacleDAO.create(spectacle);
    }

    @Path("horaires/{id}")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public IdResponse setProgrammationOfSpectacle(@PathParam("id") @NotNull int id, @Valid @NotNull List<SpectacleDAO.ProgrammationProps> programmations) {
        return spectacleDAO.setProgrammation(id, programmations);
    }

    @Path("/horaires/{id}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public IdResponse addProgrammationOfSpectacle(@PathParam("id") @NotNull int id, @Valid @NotNull SpectacleDAO.ProgrammationProps programmations){
        return spectacleDAO.addProgrammation(id, programmations);
    }

    @Path("/personnages/{spectacleId}/{personnageId}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public IdResponse addPersonnageToSpectacle(
            @PathParam("spectacleId") @NotNull int spectacleId,
            @PathParam("personnageId") @NotNull int personnageId
    ) {
        return spectacleDAO.addPersonnage(spectacleId, personnageId);
    }

    @Path("/personnages/{spectacleId}/{personnageId}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public IdResponse removePersonnageFromSpectacle(
            @PathParam("spectacleId") @NotNull int spectacleId,
            @PathParam("personnageId") @NotNull int personnageId
    ) {
        return spectacleDAO.removePersonnage(spectacleId, personnageId);
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IdResponse update(@PathParam("id") @NotNull int id, @NotNull Map<String, Object> partialData) {
        return spectacleDAO.update(id, partialData);
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public IdResponse deleteSpectacle(@PathParam("id") @NotNull int id) {
        return spectacleDAO.delete(id);
    }

}
