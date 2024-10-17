package com.example.productUploader.repository;

import com.example.productUploader.model.Integration;
import com.example.productUploader.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IntegrationRepository extends JpaRepository<Integration, Long> {
    List<Integration> findByUserId(Long userId);
    List<Integration> findAllByUser(User user); // Custom method to find all integrations by user
}
