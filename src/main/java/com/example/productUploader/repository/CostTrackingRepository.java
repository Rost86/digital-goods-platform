package com.example.productUploader.repository;

import com.example.productUploader.model.CostTracking;
import com.example.productUploader.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CostTrackingRepository extends JpaRepository<CostTracking, Long> {
    List<CostTracking> findByUser(User user);
    List<CostTracking> findByUserId(Long userId);
    Optional<CostTracking> findByYearAndMonth(int year, int month);
    Optional<CostTracking> findFirstByOrderByYearAscMonthAsc();
    Optional<CostTracking> findFirstByOrderByIdAsc();
}
