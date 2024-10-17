package com.example.productUploader.controller;

import com.example.productUploader.model.CustomerOrder;
import com.example.productUploader.model.Listing;
import com.example.productUploader.model.Buyer;
import com.example.productUploader.repository.CustomerOrderRepository;
import com.example.productUploader.repository.ListingRepository;
import com.example.productUploader.repository.BuyerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ManagementController {

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private CustomerOrderRepository customerOrderRepository;

    @Autowired
    private BuyerRepository buyerRepository;

    // Listing page
    @GetMapping("/listings")
    public String getAllListings(Model model) {
        List<Listing> listings = listingRepository.findAll(Sort.by(Sort.Direction.DESC, "listingId"));
        model.addAttribute("listings", listings);
        return "products";  // Refers to the listings view (replace if necessary)
    }

    // Display orders page
    @GetMapping("/orders")
    public String showOrdersPage(Model model) {
        // Sort by 'receipt_id' in descending order
        List<CustomerOrder> orders = customerOrderRepository.findAll(Sort.by(Sort.Direction.DESC, "receiptId"));

        model.addAttribute("orders", orders);
        return "orders";  // Return the view for orders (e.g., orders.html)
    }

    // Display buyers page
    @GetMapping("/buyers")
    public String showBuyersPage(Model model) {
        List<Buyer> buyers = buyerRepository.findAll();
        model.addAttribute("buyers", buyers);
        return "buyers";  // Return the view for buyers (e.g., buyers.html)
    }
}
