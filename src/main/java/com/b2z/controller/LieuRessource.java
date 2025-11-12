package com.b2z.controller;

import com.b2z.dao.IdResponse;
import com.b2z.dao.LieuDAO;
import com.b2z.model.Lieu;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.Map;

@Path("/lieux")
public class LieuRessource {
    private final LieuDAO lieudao = new LieuDAO();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Lieu> getAll() {
        return lieudao.findAll();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IdResponse create(LieuDAO.LieuProps lieu) {
        return lieudao.create(lieu);
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public IdResponse deleteById(@PathParam("id") int id) {
        return lieudao.delete(id);
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public IdResponse getById(@PathParam("id") int id, @NotNull Map<String, Object> partialData) {
        return lieudao.update(id, partialData);
    }


}
