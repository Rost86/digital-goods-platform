package com.example.productUploader.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Buyer {

    @Id
    @JsonProperty("buyer_user_id")
    @Column(name = "buyer_user_id", nullable = false, unique = true)
    private Long buyerUserId;

    @JsonProperty("buyer_email")
    @Column(name = "buyer_email", nullable = true)
    private String buyerEmail;

    @JsonProperty("name")
    @Column(name = "name", nullable = false)
    private String name;

    @JsonProperty("first_line")
    @Column(name = "first_line", nullable = true)
    private String firstLine;

    @JsonProperty("city")
    @Column(name = "city", nullable = true)
    private String city;

    @JsonProperty("state")
    @Column(name = "state", nullable = true)
    private String state;

    @JsonProperty("zip")
    @Column(name = "zip", nullable = true)
    private String zip;

    @JsonProperty("country_iso")
    @Column(name = "country_iso", nullable = true)
    private String countryIso;
}
