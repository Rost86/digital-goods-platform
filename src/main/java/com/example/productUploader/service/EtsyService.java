package com.example.productUploader.service;

import com.example.productUploader.model.Listing;
import com.example.productUploader.repository.ListingRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class EtsyService {

    private String accessToken;
    private static final String API_KEY = "l2z0ajfj2fkexafu7xfwb7tl"; // Add your Etsy API key here
    private static final int SHOP_ID = 48071330; // Replace with your actual shop ID (must be a number)

    @Autowired
    private ListingRepository listingRepository; // Injecting the repository for saving to the database

    @Autowired
    private ObjectMapper objectMapper; // Used for parsing JSON response into Java objects

    // Set the access token
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    // Get the access token
    public String getAccessToken() {
        return this.accessToken;
    }

    // Method to fetch products from Etsy API and save them to the database
    public String fetchAndSaveProducts() throws Exception {
        // Ensure access token is available
        if (accessToken == null || accessToken.isEmpty()) {
            throw new RuntimeException("Access token is not available. Please authenticate first.");
        }

        // Create an HTTP request to fetch active listings from Etsy
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://openapi.etsy.com/v3/application/shops/" + SHOP_ID + "/listings/active")) // Use numerical shop ID
                .header("Authorization", "Bearer " + accessToken) // Send the access token in the Authorization header
                .header("x-api-key", API_KEY) // Add the Etsy API key here
                .header("Content-Type", "application/json") // Optional: Specify that the response should be JSON
                .GET()
                .build();

        // Send the HTTP request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // If the response code is not 200, throw an exception with detailed error information
        if (response.statusCode() != 200) {
            throw new RuntimeException("Error fetching products (status code: " + response.statusCode() + "): " + response.body());
        }

        // Parse the JSON response
        JsonNode jsonNode = objectMapper.readTree(response.body());

        // Extract the "results" array from the response
        JsonNode listingsNode = jsonNode.get("results");

        // Convert the "results" array to a list of Listing objects
        Listing[] listings = objectMapper.treeToValue(listingsNode, Listing[].class);

        // Track how many listings were saved
        int savedCount = 0;

        // Iterate through the listings and save them to the database
        for (Listing listing : listings) {
            // Check if the listing already exists in the database by listingId
            if (!listingRepository.existsById(listing.getListingId())) {
                // Save only if the listing is new
                listingRepository.save(listing);
                savedCount++;
            }
        }

        // Return a message indicating how many listings were fetched and saved
        return savedCount + " listings fetched and saved successfully";
    }
}
