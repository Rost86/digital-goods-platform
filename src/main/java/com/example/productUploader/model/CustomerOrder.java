package com.example.productUploader.model;

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
public class CustomerOrder {

    @Id
    private Long receiptId;  // receipt_id is the main index

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String countryIso;

    @Column(nullable = false)
    private boolean isPaid;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "grand_total_amount")),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "grand_total_currency_code"))
    })
    private OrderAmount grandTotal;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "subtotal_amount")),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "subtotal_currency_code"))
    })
    private OrderAmount subtotal;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "total_price_amount")),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "total_price_currency_code"))
    })
    private OrderAmount totalPrice;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "total_shipping_cost_amount")),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "total_shipping_cost_currency_code"))
    })
    private OrderAmount totalShippingCost;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "total_tax_cost_amount")),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "total_tax_cost_currency_code"))
    })
    private OrderAmount totalTaxCost;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "discount_amount")),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "discount_currency_code"))
    })
    private OrderAmount discountAmt;

    private Long listingId; // for linking with the listings

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    private Buyer buyer;

    @Column(name = "create_timestamp")
    private Long createTimestamp;  // Timestamp for when the order was created
}
