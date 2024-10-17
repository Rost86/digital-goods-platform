package com.example.productUploader.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Integration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Link integration to a specific user

    @Enumerated(EnumType.STRING)  // Use EnumType.STRING to store enum as a string in the database
    @Column(nullable = false)
    private IntegrationType type; // Enum for integration type (Etsy, Printify)

    private String apiKey;           // API Key for access
    private String accessToken;      // Access Token
    private String refreshToken;     // Refresh Token
    private String clientId;         // Client ID for OAuth2
    private String clientSecret;     // Client Secret for OAuth2
    private String baseUrl;          // Base URL for API requests
    private String shopId;           // Shop ID (if applicable)
    private String additionalParams; // Any additional parameters (optional)

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;          // When the integration was created

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;          // When the integration was last updated
}
