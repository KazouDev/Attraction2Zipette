package com.b2z;

import com.b2z.utils.DBManager;
import com.fasterxml.jackson.core.util.JacksonFeature;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.validation.ValidationFeature;
import java.io.IOException;

@ApplicationPath("/")
public class AppConfig extends ResourceConfig {

    public AppConfig() {
        packages("com.b2z");

        register(ValidationFeature.class);
        register(JacksonFeature.class);

        register(CORSFilter.class);

        if (DBManager.getInstance() == null) {
            throw new RuntimeException("Failed to initialize database connection.");
        }
    }

    @Provider
    public static class CORSFilter implements ContainerResponseFilter {
        @Override
        public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
            responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
            responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
            responseContext.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
            responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
        }
    }
}
