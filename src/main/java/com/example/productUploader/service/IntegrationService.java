package com.example.productUploader.service;

import com.example.productUploader.model.Integration;
import com.example.productUploader.repository.IntegrationRepository;
import com.example.productUploader.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IntegrationService {

    @Autowired
    private IntegrationRepository integrationRepository;

    // Find integrations by user ID
    public List<Integration> getIntegrationsByUser(Long userId) {
        return integrationRepository.findByUserId(userId);
    }


    // Fetch an integration by its ID
    public Optional<Integration> getIntegrationById(Long id) {
        return integrationRepository.findById(id);
    }

    // Save or add a new integration
    public Integration save(Integration integration) {
        return integrationRepository.save(integration);
    }

    // Add a new integration linked to a user
    public Integration addIntegration(Integration integration, User user) {
        integration.setUser(user); // Link the integration to the user
        return integrationRepository.save(integration);
    }

    // Update an existing integration
    public Integration updateIntegration(Long id, Integration integrationDetails) {
        Optional<Integration> integration = integrationRepository.findById(id);
        if (integration.isPresent()) {
            Integration existingIntegration = integration.get();
            // Update fields
            existingIntegration.setApiKey(integrationDetails.getApiKey());
            existingIntegration.setAccessToken(integrationDetails.getAccessToken());
            existingIntegration.setRefreshToken(integrationDetails.getRefreshToken());
            existingIntegration.setClientId(integrationDetails.getClientId());
            existingIntegration.setClientSecret(integrationDetails.getClientSecret());
            existingIntegration.setBaseUrl(integrationDetails.getBaseUrl());
            existingIntegration.setShopId(integrationDetails.getShopId());
            existingIntegration.setAdditionalParams(integrationDetails.getAdditionalParams());
            return integrationRepository.save(existingIntegration);
        }
        return null;
    }

    // Delete an integration by ID
    public void deleteIntegration(Long id) {
        integrationRepository.deleteById(id);
    }

    // Method to find all integrations by a user
    public List<Integration> findAllByUser(User user) {
        return integrationRepository.findAllByUser(user);
    }

}
