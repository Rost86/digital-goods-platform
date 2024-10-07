package com.example.productUploader.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "listings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Listing {

    @Id
    @JsonProperty("listing_id")
    private Long listingId;  // ID of the listing from JSON

    // Field skus changed to String, as we will take only the first element from the array
    @Column(nullable = false)
    private String skus;

    @Column(nullable = false)
    @JsonProperty("user_id")
    private Long userId;  // ID of the user

    @Column(nullable = false)
    @JsonProperty("shop_id")
    private Long shopId;  // ID of the shop

    @Column(length = 255, nullable = false)
    @JsonProperty("title")
    private String title;  // Product title

    @Column(columnDefinition = "TEXT")
    @JsonProperty("description")
    private String description;  // Product description

    @Column(nullable = false)
    @JsonProperty("state")
    private String state;  // State of the listing (e.g., "active")

    @Column(nullable = false)
    private Timestamp creationTimestamp;  // Creation time

    @Column(nullable = false)
    private Timestamp lastModifiedTimestamp;  // Last modification time

    // Method to convert Unix Timestamp to Timestamp object for creationTimestamp field
    @JsonProperty("creation_timestamp")
    public void setCreationTimestamp(long creationTimestamp) {
        this.creationTimestamp = Timestamp.from(java.time.Instant.ofEpochSecond(creationTimestamp));
    }

    // Method to convert Unix Timestamp to Timestamp object for lastModifiedTimestamp field
    @JsonProperty("last_modified_timestamp")
    public void setLastModifiedTimestamp(long lastModifiedTimestamp) {
        this.lastModifiedTimestamp = Timestamp.from(java.time.Instant.ofEpochSecond(lastModifiedTimestamp));
    }

    @Column(nullable = false)
    @JsonProperty("quantity")
    private int quantity;  // Product quantity

    @Column(nullable = true)
    @JsonProperty("shop_section_id")
    private Long shopSectionId;  // Shop section ID

    @Column(nullable = true)
    @JsonProperty("featured_rank")
    private int featuredRank;  // Listing rank (e.g., for promotion)

    @Column(nullable = false)
    @JsonProperty("url")
    private String url;  // Product URL

    @Column(nullable = true)
    @JsonProperty("num_favorers")
    private int numFavorers;  // Number of favorites

    @Column(nullable = false)
    @JsonProperty("non_taxable")
    private boolean nonTaxable;  // Whether the product is non-taxable

    @Column(nullable = false)
    @JsonProperty("is_taxable")
    private boolean isTaxable;  // Whether the product is taxable

    @Column(nullable = false)
    @JsonProperty("is_customizable")
    private boolean isCustomizable;  // Whether the product can be customized

    @Column(nullable = false)
    @JsonProperty("is_personalizable")
    private boolean isPersonalizable;  // Whether the product can be personalized

    @Column(nullable = false)
    @JsonProperty("personalization_is_required")
    private boolean personalizationIsRequired;  // Is personalization required

    @Column(nullable = true)
    @JsonProperty("personalization_char_count_max")
    private Integer personalizationCharCountMax;  // Max characters for personalization

    @Column(columnDefinition = "TEXT")
    @JsonProperty("personalization_instructions")
    private String personalizationInstructions;  // Personalization instructions

    @Column(nullable = false)
    @JsonProperty("listing_type")
    private String listingType;  // Listing type (e.g., "download")

    @ElementCollection
    @Column(name = "tags")
    @JsonProperty("tags")
    private List<String> tags;  // Product tags

    @ElementCollection
    @Column(name = "materials")
    @JsonProperty("materials")
    private List<String> materials;  // Product materials

    @Column(nullable = true)
    @JsonProperty("shipping_profile_id")
    private Long shippingProfileId;  // Shipping profile ID

    @Column(nullable = true)
    @JsonProperty("return_policy_id")
    private Integer returnPolicyId;  // Return policy ID

    @Column(nullable = true)
    @JsonProperty("processing_min")
    private Integer processingMin;  // Minimum processing time

    @Column(nullable = true)
    @JsonProperty("processing_max")
    private Integer processingMax;  // Maximum processing time

    @Column(nullable = false)
    @JsonProperty("who_made")
    private String whoMade;  // Who made the product

    @Column(nullable = false)
    @JsonProperty("when_made")
    private String whenMade;  // When the product was made

    @Column(nullable = false)
    @JsonProperty("is_supply")
    private boolean isSupply;  // Is the product a supply item

    @Column(nullable = true)
    @JsonProperty("item_weight")
    private Double itemWeight;  // Product weight

    @Column(nullable = true)
    @JsonProperty("item_weight_unit")
    private String itemWeightUnit;  // Weight unit for the product

    @Column(nullable = true)
    @JsonProperty("item_length")
    private Double itemLength;  // Product length

    @Column(nullable = true)
    @JsonProperty("item_width")
    private Double itemWidth;  // Product width

    @Column(nullable = true)
    @JsonProperty("item_height")
    private Double itemHeight;  // Product height

    @Column(nullable = true)
    @JsonProperty("item_dimensions_unit")
    private String itemDimensionsUnit;  // Dimension unit for the product

    @Column(nullable = false)
    @JsonProperty("is_private")
    private boolean isPrivate;  // Whether the listing is private

    @ElementCollection
    @Column(name = "style")
    @JsonProperty("style")
    private List<String> style;  // Product styles

    @Column(columnDefinition = "TEXT")
    @JsonProperty("file_data")
    private String fileData;  // File data (e.g., ZIP files)

    @Column(nullable = false)
    @JsonProperty("has_variations")
    private boolean hasVariations;  // Does the product have variations

    @Column(nullable = false)
    @JsonProperty("should_auto_renew")
    private boolean shouldAutoRenew;  // Should the listing auto-renew

    @Column(nullable = false)
    @JsonProperty("language")
    private String language;  // Language of the listing

    @Column(nullable = false)
    @JsonProperty("taxonomy_id")
    private int taxonomyId;  // Taxonomy ID

    @ElementCollection
    @JsonProperty("production_partners")
    private List<String> productionPartners;  // Production partners

    @Column(nullable = false)
    @JsonProperty("views")
    private int views;  // Number of views

    // Fields for price
    @Column(nullable = false)
    private int priceAmount;  // Price amount of the product

    @Column(nullable = false)
    private String priceCurrency;  // Currency of the product price

    // Unpack the JSON object "price" into individual fields
    @JsonProperty("price")
    private void unpackPrice(Map<String, Object> price) {
        this.priceAmount = (Integer) price.get("amount");  // Extract price amount
        this.priceCurrency = (String) price.get("currency_code");  // Extract currency code
    }

    // Unpack the skus array and store the first element as a string
    @JsonProperty("skus")
    private void unpackSkus(List<String> skus) {
        if (skus != null && !skus.isEmpty()) {
            this.skus = skus.get(0);  // Store the first SKU
        }
    }
}
