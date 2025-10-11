package com.b2z;
import com.fasterxml.jackson.core.util.JacksonFeature;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.validation.ValidationFeature;

@ApplicationPath("/")
public class AppConfig extends ResourceConfig {

    public AppConfig() {
        packages("com.b2z");

        register(ValidationFeature.class);
        register(JacksonFeature.class);
    }

}
