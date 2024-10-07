package com.example.productUploader.controller;

import com.example.productUploader.service.EtsyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class EtsyController {

    @Autowired
    private EtsyService etsyService;

    // Endpoint to receive access token from Laravel
    @PostMapping("/etsy/token")
    public String receiveAccessToken(@RequestBody Map<String, Object> payload) {
        // Log entire JSON body
        System.out.println("Received JSON Body:");
        for (String key : payload.keySet()) {
            System.out.println(key + ": " + payload.get(key));
        }

        // Access specific fields
        String accessToken = (String) payload.get("access_token");
        System.out.println("Access Token: " + accessToken);

        // Set the access token in the EtsyService
        etsyService.setAccessToken(accessToken);

        return "Access token received successfully";
    }

    // Endpoint to fetch products from Etsy
    @GetMapping("/etsy/products")
    public String fetchProducts() throws Exception {
        try {
            // Fetch products using the access token stored in the service
            String products = etsyService.fetchAndSaveProducts();
            return products;
        } catch (Exception e) {
            // Handle any exceptions and return an error message
            return "Error fetching products: " + e.getMessage();
        }
    }
}
