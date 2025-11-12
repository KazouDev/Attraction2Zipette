package com.b2z.controller;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/")
public class HealRessource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String heal() {
        return "OK";
    }
}
