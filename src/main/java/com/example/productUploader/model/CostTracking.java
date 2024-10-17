package com.example.productUploader.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cost_tracking")
public class CostTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int year;  // Year of the cost
    private int month;  // Month of the cost

    private BigDecimal fixedCost;  // Monthly fixed cost (e.g., software, hosting)
    private BigDecimal variableCost;  // Monthly variable cost (e.g., marketing, transaction fees)
    private BigDecimal costPerUnit;  // Cost per unit of production

    private BigDecimal advertisingCost;  // New field for advertising cost

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // Link the cost entry to a specific user
}