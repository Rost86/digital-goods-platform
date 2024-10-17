package com.example.productUploader.service;

import com.example.productUploader.model.CostTracking;
import com.example.productUploader.model.User;
import com.example.productUploader.repository.CostTrackingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CostTrackingService {


    private final CostTrackingRepository costTrackingRepository;

    @Autowired
    public CostTrackingService(CostTrackingRepository costTrackingRepository) {
        this.costTrackingRepository = costTrackingRepository;
    }

    // Get all costs for a specific user
    public List<CostTracking> getCostsByUser(User user) {
        return costTrackingRepository.findByUser(user);
    }

    // Get all costs for a specific user by userId
    public List<CostTracking> getCostsByUserId(Long userId) {
        return costTrackingRepository.findByUserId(userId);
    }

    // Get all cost entries
    public List<CostTracking> getAllCosts() {
        return costTrackingRepository.findAll();
    }

    // Get cost by ID
    public Optional<CostTracking> getCostById(Long id) {
        return costTrackingRepository.findById(id);
    }

    public Optional<CostTracking> getCostByYearAndMonth(int year, int month) {
        return costTrackingRepository.findByYearAndMonth(year, month);
    }

    // Create or update cost
    public CostTracking saveOrUpdateCost(CostTracking costTracking) {
        return costTrackingRepository.save(costTracking);
    }

    // Delete cost entry
    public void deleteCost(Long id) {
        costTrackingRepository.deleteById(id);
    }

    public Optional<CostTracking> findById(Long id) {
        return costTrackingRepository.findById(id);
    }

    public Optional<CostTracking> getFirstCostEntry() {
        return costTrackingRepository.findFirstByOrderByIdAsc();
    }

    // Update specific cost fields
    public CostTracking updateCost(Long id, BigDecimal fixedCost, BigDecimal variableCost, BigDecimal costPerUnit, BigDecimal advertisingCost) {
        CostTracking costTracking = costTrackingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cost tracking not found for id: " + id));

        costTracking.setFixedCost(fixedCost);
        costTracking.setVariableCost(variableCost);
        costTracking.setCostPerUnit(costPerUnit);
        costTracking.setAdvertisingCost(advertisingCost);

        return costTrackingRepository.save(costTracking);
    }
}


