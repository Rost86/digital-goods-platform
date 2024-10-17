package com.example.productUploader.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "customer_order")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerOrder {

    @Id
    @JsonProperty("receipt_id")  // Maps JSON "receipt_id" to this field
    private Long receiptId;  // receipt_id is the main index

    @Column(nullable = false)
    @JsonProperty("status")  // Maps JSON "status"
    private String status;

    @Column(nullable = true)
    @JsonProperty("country_iso")
    private String countryIso;

    @Column(nullable = false)
    @JsonProperty("is_paid")  // Maps JSON "is_paid"
    private boolean isPaid;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "grand_total_amount")),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "grand_total_currency_code"))
    })
    @JsonProperty("grandtotal")  // Maps JSON "grandtotal"
    private OrderAmount grandTotal;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "subtotal_amount")),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "subtotal_currency_code"))
    })
    @JsonProperty("subtotal")  // Maps JSON "subtotal"
    private OrderAmount subtotal;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "total_price_amount")),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "total_price_currency_code"))
    })
    @JsonProperty("total_price")  // Maps JSON "total_price"
    private OrderAmount totalPrice;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "total_shipping_cost_amount")),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "total_shipping_cost_currency_code"))
    })
    @JsonProperty("total_shipping_cost")  // Maps JSON "total_shipping_cost"
    private OrderAmount totalShippingCost;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "total_tax_cost_amount")),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "total_tax_cost_currency_code"))
    })
    @JsonProperty("total_tax_cost")  // Maps JSON "total_tax_cost"
    private OrderAmount totalTaxCost;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "discount_amount")),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "discount_currency_code"))
    })
    @JsonProperty("discount_amt")  // Maps JSON "discount_amt"
    private OrderAmount discountAmt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    private Buyer buyer;  // Relationship to Buyer

    @ManyToOne
    @JoinColumn(name = "listing_id", referencedColumnName = "listing_id")
    private Listing listing;

    @Column(name = "create_timestamp")
    @JsonProperty("create_timestamp")  // Maps JSON "create_timestamp"
    private Long createTimestamp;  // Timestamp for when the order was created

    @Column(name = "updated_timestamp")
    @JsonProperty("updated_timestamp")
    private Long updatedTimestamp;

    @Column(name = "source", nullable = false)
    private String source;
}
