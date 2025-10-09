package com.b2z.controller;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/")
public class HealRessource {
    @GET
    public String heal() {
        return "OK";
    }
}
