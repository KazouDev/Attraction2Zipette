package com.b2z.controller;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/favicon.ico")
public class FaviconResource {
    @GET
    public Response getFavicon() {
        return Response.status(204).build();
    }
}
