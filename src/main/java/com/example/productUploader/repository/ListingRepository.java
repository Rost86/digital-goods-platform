package com.example.productUploader.repository;

import com.example.productUploader.model.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {
    List<Listing> findTop10ByOrderByViewsDesc();
}