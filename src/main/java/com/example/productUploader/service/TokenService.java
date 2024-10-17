package com.example.productUploader.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class TokenService {

    private static final String CLIENT_ID = "l2z0ajfj2fkexafu7xfwb7tl";  // Your Etsy Client ID
    private static final String CLIENT_SECRET = "ws3ztj2wyh";  // Your Etsy Client Secret
    private static final String REFRESH_TOKEN_URL = "https://api.etsy.com/v3/public/oauth/token";

    @Autowired
    private ObjectMapper objectMapper;  // For parsing JSON responses

    /*
     * This method will be used to refresh the access token once the application
     * is live and tokens are stored in the database. For now, tokens are
     * retrieved via Laravel and sent to this service.
     *
     * How to enable:
     * 1. Uncomment this method.
     * 2. Ensure tokens are saved and fetched from the database.
     * 3. Replace the refreshToken parameter with the value from the database.
     * 4. Call this method whenever the access token expires to refresh it.
     */
    /*
    public String refreshAccessToken(String refreshToken) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(REFRESH_TOKEN_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=refresh_token"
                        + "&client_id=" + CLIENT_ID
                        + "&client_secret=" + CLIENT_SECRET
                        + "&refresh_token=" + refreshToken))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to refresh token: " + response.body());
        }

        // Parse JSON response to get the new access token
        JsonNode responseBody = objectMapper.readTree(response.body());
        return responseBody.get("access_token").asText();
    }
    */

    /*
     * INSTRUCTIONS FOR PRODUCTION TRANSITION:
     * 1. When migrating to production, uncomment the above refreshAccessToken method.
     * 2. Store access tokens and refresh tokens in the database.
     * 3. Set up a scheduled task to periodically check and refresh tokens before they expire.
     * 4. Integrate this service with other parts of the application where token usage is needed.
     * 5. Make sure to handle any potential failures when refreshing tokens (retry mechanism).
     */


    public String receiveAccessTokenFromLaravel(String accessToken) {

        System.out.println("Access Token received from Laravel: " + accessToken);

        return accessToken;
    }

    /*
    public void checkAndRefreshToken(String currentRefreshToken) {
        // Check if the token needs to be refreshed (you can track expiration in your DB)
        // If expired, call refreshAccessToken(currentRefreshToken);
    }
    */
}
