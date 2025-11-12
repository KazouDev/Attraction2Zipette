package com.b2z.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ApiClient {

    private final ResourceBundle properties;
    private final String baseURL;
    private @Nullable String apiKey;

    private static final Gson gson = new Gson();
    private static final HttpClient client = HttpClient.newHttpClient();

    protected enum HttpMethod {
        GET, POST, PUT, DELETE
    }

    public ApiClient(@NotNull String baseURL) {
        properties = ResourceBundle.getBundle("config");
        this.baseURL = baseURL;
    }

    protected void setAPIKey(@NotNull String apiKey) {
        this.apiKey = apiKey;
    }

    protected ResourceBundle getConfig() {
        return properties;
    }

    protected JsonElement sendRequest(
            @NotNull HttpMethod method,
            @NotNull String endpoint
    ) throws IOException, InterruptedException {
        return this.sendRequest(method, endpoint, null, null);
    }

    protected JsonElement sendRequest(
            @NotNull HttpMethod method,
            @NotNull Map<String, String> queryParams
    ) throws IOException, InterruptedException {
        return this.sendRequest(method, null, queryParams, null);
    }

    protected JsonElement sendRequest(
            @NotNull HttpMethod method,
            @NotNull String endpoint,
            @NotNull Map<String, String> queryParams
    ) throws IOException, InterruptedException {
        return this.sendRequest(method, endpoint, queryParams, null);
    }

    protected JsonElement sendRequest(
            @NotNull HttpMethod method,
            @Nullable String endpoint,
            @Nullable Map<String, String> queryParams,
            @Nullable Gson body
    ) throws IOException, InterruptedException {
        String url = this.baseURL;

        if(endpoint != null) {
            if(!endpoint.startsWith("/")) {
                url += "/" + endpoint;
            } else {
                url += endpoint;
            }
        }

        if(queryParams == null) {
            queryParams = new HashMap<>();
        }

        if(this.apiKey != null) {
            queryParams.put("API_KEY", this.apiKey);
        }

        if (!queryParams.isEmpty()) {
            String query = queryParams.entrySet().stream()
                    .map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8) + "="
                            + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                    .reduce((a, b) -> a + "&" + b)
                    .orElse("");
            url += "?" + query;
        }

        HttpRequest.Builder builder = HttpRequest
                .newBuilder()
                .uri(URI.create(url));

        final String jsonBody = (body != null) ? gson.toJson(body) : null;

        switch (method) {
            case GET -> builder.GET();
            case POST -> builder
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody != null ? jsonBody : ""));
            case PUT -> builder
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody != null ? jsonBody : ""));
            case DELETE -> builder.DELETE();
        }

        HttpRequest request = builder.build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        final String responseBody = response.body();
        if(response.statusCode() != 200) {
            throw new IOException("Invalid status code returned: " + response.statusCode() + ": " + responseBody);
        }

        return (responseBody.startsWith("{") || responseBody.startsWith("[")) ? JsonParser.parseString(responseBody) : null;
    }

}
