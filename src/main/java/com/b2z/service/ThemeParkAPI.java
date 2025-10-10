package com.b2z.service;

import com.b2z.utils.ApiClient;
import com.b2z.utils.Utils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jakarta.validation.constraints.NotNull;

import java.io.IOException;

public class ThemeParkAPI extends ApiClient {

    private final String GROUP_NAME = "team1";
    private static ThemeParkAPI instance;

    public static ThemeParkAPI getInstance() {
        if(instance == null) {
            instance = new ThemeParkAPI();
        }
        return instance;
    }

    public ThemeParkAPI() {
        super("http://vps817240.ovh.net:8081/themepark");
        this.setAPIKey(this.getConfig().getString("THEME_PARK_API_KEY"));
    }

    public JsonObject getCrowds(@NotNull String attractionCode) throws IOException, InterruptedException {
        JsonElement result = this.sendRequest(
                HttpMethod.GET,
                Utils.map(
                        "groupName", GROUP_NAME,
                        "attractionCode", attractionCode
                )
        );
        return result.getAsJsonObject();
    }

    public JsonObject addAttraction(@NotNull String attractionName, @NotNull String attractionCode) throws IOException, InterruptedException {
        JsonElement result = this.sendRequest(
                HttpMethod.PUT,
                Utils.map(
                        "groupName", GROUP_NAME,
                        "attractionName", attractionName,
                        "attractionCode", attractionCode
                )
        );
        return result.getAsJsonObject();
    }

}
