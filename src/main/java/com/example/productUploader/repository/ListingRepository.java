package com.example.productUploader.repository;

import com.example.productUploader.model.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {
    // No need to add methods here; JpaRepository provides basic CRUD operations.
}