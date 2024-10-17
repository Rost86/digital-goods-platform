package com.example.productUploader.controller;

import com.example.productUploader.service.EtsyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/etsy")
public class EtsyController {

    private final EtsyService etsyService;

    @Autowired
    public EtsyController(EtsyService etsyService) {
        this.etsyService = etsyService;
    }

    /**
     * Endpoint to receive and store access token in the user's integration (from Laravel).
     *
     * @param userId  The ID of the user for whom the token is being stored.
     * @param payload The JSON payload containing the access token (and optionally refresh token).
     * @return A success or error message.
     */
    @PostMapping("/token/{userId}")
    public String receiveAccessToken(@PathVariable Long userId, @RequestBody Map<String, Object> payload) {
        try {
            // Debugging: Log the entire JSON body
            System.out.println("Received JSON Body:");
            payload.forEach((key, value) -> System.out.println(key + ": " + value));

            // Extract access token from the payload
            String accessToken = (String) payload.get("access_token");

            // Optional: Extract refresh token if present
            String refreshToken = (String) payload.getOrDefault("refresh_token", null);

            // Store the access token and refresh token for the specified user
            etsyService.updateTokens(userId, accessToken, refreshToken);

            return "Access token received and stored successfully";
        } catch (Exception e) {
            // Return detailed error message
            return "Error storing access token: " + e.getMessage();
        }
    }

    /**
     * Endpoint to fetch products from Etsy for a specific user.
     *
     * @param userId The ID of the user whose products are being fetched.
     * @return The fetched products or an error message.
     * @throws Exception If there is an error while fetching the products.
     */
    @GetMapping("/products/{userId}")
    public String fetchProducts(@PathVariable Long userId) throws Exception {
        try {
            // Fetch products using the stored access token for the user
            String products = etsyService.fetchAndSaveProducts(userId);
            return products;
        } catch (Exception e) {
            // Handle any exceptions and return an error message
            return "Error fetching products: " + e.getMessage();
        }
    }

    /**
     * Endpoint to fetch orders from Etsy for a specific user.
     *
     * @param userId The ID of the user whose orders are being fetched.
     * @return The fetched orders or an error message.
     * @throws Exception If there is an error while fetching the orders.
     */
    @GetMapping("/orders/{userId}")
    public String fetchOrders(@PathVariable Long userId) throws Exception {
        try {
            // Fetch orders using the stored access token for the user
            String orders = etsyService.fetchAndSaveOrders(userId);
            return orders;
        } catch (Exception e) {
            // Handle any exceptions and return an error message
            return "Error fetching orders: " + e.getMessage();
        }
    }
    /**
     * Endpoint to fetch and save both products and orders from Etsy for a specific user.
     *
     * @param userId The ID of the user whose products and orders are being fetched.
     * @return A message indicating the result of the operation.
     */

    @GetMapping("/sync/{userId}")
    public String syncProductsAndOrders(@PathVariable Long userId) {
        try {
            // First, fetch and save products
            String productsResult = etsyService.fetchAndSaveProducts(userId);

            // Then, fetch and save orders
            String ordersResult = etsyService.fetchAndSaveOrders(userId);

            // Return combined result messages
            return "Sync completed: \n" + productsResult + "\n" + ordersResult;
        } catch (Exception e) {
            return "Error syncing products and orders: " + e.getMessage();
        }
    }
    /**
     * Future implementation to refresh access token using the refresh token.
     *
     * @param userId The ID of the user whose access token is being refreshed.
     * @return The refreshed access token or an error message.
     * @throws Exception If there is an error while refreshing the token.
     */
    /*
     * @PostMapping("/refresh-token/{userId}")
     * public String refreshAccessToken(@PathVariable Long userId) throws Exception {
     *     try {
     *         // Refresh the access token and store the updated token in the database
     *         String newAccessToken = etsyService.refreshAccessToken(userId);
     *         return "Access token refreshed successfully: " + newAccessToken;
     *     } catch (Exception e) {
     *         return "Error refreshing access token: " + e.getMessage();
     *     }
     * }
     */
}
