package com.example.productUploader.controller;

import com.example.productUploader.model.Integration;
import com.example.productUploader.model.User;
import com.example.productUploader.service.IntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/integrations")
public class IntegrationController {

    @Autowired
    private IntegrationService integrationService;

    // Get all integrations for a user
    @GetMapping("/user/{userId}")
    public List<Integration> getIntegrationsByUser(@PathVariable Long userId) {
        return integrationService.getIntegrationsByUser(userId);
    }

    // Get a specific integration by ID
    @GetMapping("/{id}")
    public Integration getIntegrationById(@PathVariable Long id) {
        return integrationService.getIntegrationById(id)
                .orElseThrow(() -> new RuntimeException("Integration not found with id: " + id));
    }

    // Create a new integration
    @PostMapping("/user/{userId}")
    public Integration addIntegration(@PathVariable Long userId, @RequestBody Integration integration) {
        // Here you would typically load the User entity from the database
        User user = new User(); // Dummy user object; replace with actual lookup
        user.setId(userId); // Set the user ID

        return integrationService.addIntegration(integration, user);
    }

    // Update an existing integration
    @PutMapping("/{id}")
    public Integration updateIntegration(@PathVariable Long id, @RequestBody Integration integration) {
        return integrationService.updateIntegration(id, integration);
    }

    // Delete an integration
    @DeleteMapping("/{id}")
    public void deleteIntegration(@PathVariable Long id) {
        integrationService.deleteIntegration(id);
    }
}
