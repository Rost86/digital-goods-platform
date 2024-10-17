package com.example.productUploader.service;

import com.example.productUploader.model.Buyer;
import com.example.productUploader.model.CustomerOrder;
import com.example.productUploader.model.Integration;
import com.example.productUploader.model.OrderAmount;
import com.example.productUploader.model.Listing;
import com.example.productUploader.repository.BuyerRepository;
import com.example.productUploader.repository.IntegrationRepository;
import com.example.productUploader.repository.ListingRepository;
import com.example.productUploader.repository.OrderRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EtsyService {

    @Autowired
    private IntegrationRepository integrationRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BuyerRepository buyerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Fetch the Etsy integration for a specific user.
     *
     * @param userId The ID of the user whose Etsy integration is needed.
     * @return The user's Etsy integration details.
     */
    private Integration getEtsyIntegrationForUser(Long userId) {
        Optional<Integration> integrationOpt = integrationRepository.findByUserId(userId).stream()
                .filter(integration -> "https://openapi.etsy.com/v3".equals(integration.getBaseUrl()))  // Match with actual Etsy base URL
                .findFirst();
        return integrationOpt.orElseThrow(() -> new RuntimeException("Etsy integration not found for user ID: " + userId));
    }

    /**
     * Fetch and save products from Etsy using the stored access token.
     *
     * @param userId The ID of the user whose products are being fetched.
     * @return The fetched products in JSON format (or modify to handle saving).
     * @throws Exception If there is an error during the API request.
     */
    public String fetchAndSaveProducts(Long userId) throws Exception {
        Integration integration = getEtsyIntegrationForUser(userId);
        String accessToken = integration.getAccessToken();
        String apiKey = integration.getApiKey();
        String baseUrl = integration.getBaseUrl();
        String shopId = integration.getShopId();

        // Ensure access token is available
        if (accessToken == null || accessToken.isEmpty()) {
            throw new RuntimeException("Access token is not available. Please authenticate first.");
        }

        HttpClient client = HttpClient.newHttpClient();

        int limit = 100;  // Maximum number of listings to retrieve per page
        int offset = 0;   // Start from the first listing
        int totalListingsFetched = 0;
        int savedCount = 0;

        boolean morePages = true;

        while (morePages) {
            // Build the request URL with limit and offset for pagination
            String requestUrl = baseUrl + "/application/shops/" + shopId + "/listings/active?limit=" + limit + "&offset=" + offset;

            // Build the request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("x-api-key", apiKey)
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Handle non-successful response
            if (response.statusCode() != 200) {
                throw new RuntimeException("Error fetching products (status code: " + response.statusCode() + "): " + response.body());
            }

            // Parse the JSON response
            JsonNode jsonNode = objectMapper.readTree(response.body());
            JsonNode listingsNode = jsonNode.get("results");

            // If there are no more results, stop the loop
            if (listingsNode.size() == 0) {
                morePages = false;
            } else {
                // Update total listings fetched and move to the next page
                totalListingsFetched += listingsNode.size();
                offset += limit;

                // Convert the "results" array to a list of Listing objects
                Listing[] listings = objectMapper.treeToValue(listingsNode, Listing[].class);

                // Iterate through the listings and save them to the database
                for (Listing listing : listings) {
                    Optional<Listing> existingListingOpt = listingRepository.findById(listing.getListingId());
                    if (existingListingOpt.isPresent()) {
                        Listing existingListing = existingListingOpt.get();
                        if (!existingListing.getUpdatedTimestamp().equals(listing.getUpdatedTimestamp())) {
                            listingRepository.save(listing);
                            savedCount++;
                        }
                    } else {
                        listingRepository.save(listing);
                        savedCount++;
                    }
                }
            }
        }

        // Return a message indicating the total listings fetched and saved
        return totalListingsFetched + " listings fetched, " + savedCount + " listings saved successfully";
    }

    /**
     * Fetch and save orders from Etsy using the stored access token.
     *
     * @param userId The ID of the user whose orders are being fetched.
     * @return The fetched orders in JSON format (or modify to handle saving).
     * @throws Exception If there is an error during the API request.
     */
    @Transactional
    public String fetchAndSaveOrders(Long userId) throws Exception {
        Integration integration = getEtsyIntegrationForUser(userId);
        String accessToken = integration.getAccessToken();
        String apiKey = integration.getApiKey();
        String baseUrl = integration.getBaseUrl();
        String shopId = integration.getShopId();

        // Ensure access token is available
        if (accessToken == null || accessToken.isEmpty()) {
            throw new RuntimeException("Access token is not available. Please authenticate first.");
        }

        HttpClient client = HttpClient.newHttpClient();

        int limit = 100;  // Maximum number of orders to retrieve per page
        int offset = 0;   // Start from the first order
        boolean morePages = true;
        int totalOrdersFetched = 0;
        int savedOrdersCount = 0;
        int updatedOrdersCount = 0;

        while (morePages) {
            // Build the request URL for fetching orders
            String requestUrl = baseUrl + "/application/shops/" + shopId + "/receipts?limit=" + limit + "&offset=" + offset;

            // Build the request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("x-api-key", apiKey)
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Error fetching orders (status code: " + response.statusCode() + "): " + response.body());
            }

            // Parse the JSON response
            JsonNode jsonNode = objectMapper.readTree(response.body());
            JsonNode ordersNode = jsonNode.get("results");

            if (ordersNode == null || ordersNode.size() == 0) {
                morePages = false;
            } else {
                totalOrdersFetched += ordersNode.size();
                offset += limit;

                for (JsonNode orderNode : ordersNode) {
                    try {
                        Long receiptId = orderNode.get("receipt_id").asLong();
                        // Find existing order or create new
                        CustomerOrder order = orderRepository.findById(receiptId).orElse(new CustomerOrder());

                        // Update order fields
                        order.setReceiptId(receiptId);
                        order.setStatus(orderNode.get("status").asText());
                        order.setCountryIso(orderNode.get("country_iso").asText(null)); // Null if not present
                        order.setPaid(orderNode.get("is_paid").asBoolean());
                        order.setCreateTimestamp(orderNode.get("create_timestamp").asLong());

                        // Process order amounts
                        order.setSubtotal(new OrderAmount(
                                orderNode.get("subtotal").get("amount").asInt(),
                                orderNode.get("subtotal").get("currency_code").asText()));

                        order.setGrandTotal(new OrderAmount(
                                orderNode.get("grandtotal").get("amount").asInt(),
                                orderNode.get("grandtotal").get("currency_code").asText()));

                        order.setTotalPrice(new OrderAmount(
                                orderNode.get("total_price").get("amount").asInt(),
                                orderNode.get("total_price").get("currency_code").asText()));

                        order.setDiscountAmt(new OrderAmount(
                                orderNode.get("discount_amt").get("amount").asInt(),
                                orderNode.get("discount_amt").get("currency_code").asText()));

                        // Process Buyer information
                        Buyer buyer = buyerRepository.findById(orderNode.get("buyer_user_id").asLong())
                                .orElse(new Buyer());

                        buyer.setBuyerUserId(orderNode.get("buyer_user_id").asLong());
                        buyer.setName(orderNode.get("name").asText());
                        buyer.setFirstLine(orderNode.get("first_line").asText());
                        buyer.setCity(orderNode.get("city").asText());
                        buyer.setState(orderNode.get("state").asText());
                        buyer.setZip(orderNode.get("zip").asText());
                        buyer.setCountryIso(orderNode.get("country_iso").asText());

                        // Save or update Buyer
                        buyerRepository.save(buyer);
                        order.setBuyer(buyer);

                        // Process listing_id from transactions (assuming only one transaction per order)
                        JsonNode transactionsNode = orderNode.get("transactions");
                        if (transactionsNode != null && transactionsNode.size() > 0) {
                            JsonNode transaction = transactionsNode.get(0);  // Get the first (and only) transaction
                            Long listingId = transaction.get("listing_id").asLong();
                            System.out.println("Listing ID: " + listingId);  // Logging listing_id
                            Listing listing = listingRepository.findById(listingId)
                                    .orElseThrow(() -> new RuntimeException("Listing not found with id: " + listingId));
                            order.setListing(listing);
                        }

                        // Set updated_timestamp
                        Long updatedTimestamp = orderNode.get("updated_timestamp").asLong();
                        System.out.println("Updated Timestamp: " + updatedTimestamp);  // Logging updated_timestamp
                        order.setUpdatedTimestamp(updatedTimestamp);

                        order.setSource("Etsy");

                        // Save or update order
                        orderRepository.save(order);

                        // Track saved or updated counts
                        if (orderRepository.existsById(receiptId)) {
                            updatedOrdersCount++;
                        } else {
                            savedOrdersCount++;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();  // Log the full error stack trace
                    }
                }
            }
        }

        return totalOrdersFetched + " orders fetched, " + savedOrdersCount + " new orders saved, " + updatedOrdersCount + " orders updated successfully";
    }

    /**
     * Update the user's access and refresh tokens in the database.
     *
     * @param userId The ID of the user whose tokens are being updated.
     * @param accessToken The new access token.
     * @param refreshToken The new refresh token (optional).
     */
    public void updateTokens(Long userId, String accessToken, String refreshToken) {
        Integration integration = getEtsyIntegrationForUser(userId);
        integration.setAccessToken(accessToken);

        // Only update refresh token if it is provided
        if (refreshToken != null) {
            integration.setRefreshToken(refreshToken);
        }

        integrationRepository.save(integration);
    }
}
