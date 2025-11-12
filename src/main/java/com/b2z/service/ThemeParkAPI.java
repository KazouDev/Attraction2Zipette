package com.b2z.service;

import com.b2z.utils.ApiClient;
import com.b2z.utils.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jakarta.validation.constraints.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ThemeParkAPI extends ApiClient {

    public record AttractionCrowd(String attractionCode, String attractionName, Integer waitDuration) {}
    private final String GROUP_NAME = "team1";
    private static ThemeParkAPI instance;
    private final Map<String, AttractionCrowd> crowds;

    public static ThemeParkAPI getInstance() {
        if(instance == null) {
            instance = new ThemeParkAPI();
        }
        return instance;
    }

    public ThemeParkAPI() {
        super("http://vps817240.ovh.net:8081/themepark");
        this.setAPIKey(this.getConfig().getString("THEME_PARK_API_KEY"));

        this.crowds = new HashMap<>();
        this.runTask();
    }

    public Map<String, AttractionCrowd> getCrowds() {
        return this.crowds;
    }

    public void addAttraction(@NotNull String attractionName, @NotNull String attractionCode) {
        try {
            this.sendRequest(
                    HttpMethod.PUT,
                    "/attractions",
                    Utils.map(
                            "groupName", GROUP_NAME,
                            "attractionName", attractionName,
                            "attractionCode", attractionCode
                    )
            );
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void removeAttraction(@NotNull String attractionCode) {
        try {
            this.sendRequest(
                    HttpMethod.DELETE,
                    "/attractions",
                    Utils.map(
                            "groupName", GROUP_NAME,
                            "attractionCode", attractionCode
                    )
            );
            System.out.println("removing attraction " + attractionCode);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void runTask() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                try {
                    JsonElement response = sendRequest(HttpMethod.GET, "/crowds/"+ GROUP_NAME);
                    JsonArray result = response.getAsJsonArray();

                    for (JsonElement jsonElement : result) {
                        JsonObject object = jsonElement.getAsJsonObject();
                        AttractionCrowd attractionCrowd = new AttractionCrowd(
                                object.get("attractionCode").getAsString(),
                                object.get("attractionName").getAsString(),
                                object.get("waitDuration").getAsInt()
                        );
                        crowds.put(attractionCrowd.attractionCode(), attractionCrowd);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 120000); // 2 minutes
    }

}
