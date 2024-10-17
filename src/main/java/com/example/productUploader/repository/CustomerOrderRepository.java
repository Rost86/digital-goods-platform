package com.example.productUploader.repository;

import com.example.productUploader.model.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {

    // Query to get total revenue for all time
    @Query("SELECT SUM(o.subtotal.amount) FROM CustomerOrder o")
    BigDecimal getTotalRevenue();

    // Query to get total revenue for the last month, using Unix timestamp comparison
    @Query("SELECT SUM(o.subtotal.amount) FROM CustomerOrder o WHERE o.createTimestamp >= :startDateTimestamp")
    BigDecimal getTotalRevenueForLastMonth(@Param("startDateTimestamp") Long startDateTimestamp);

    // Query to count total number of orders for all time
    @Query("SELECT COUNT(o) FROM CustomerOrder o")
    Long getTotalOrders();

    // Query to count total number of orders for the last month
    @Query("SELECT COUNT(o) FROM CustomerOrder o WHERE o.createTimestamp >= :startDateTimestamp")
    Long getTotalOrdersForLastMonth(@Param("startDateTimestamp") Long startDateTimestamp);

    // Query to get revenue for the last 6 months, grouped by month
    @Query(value = "SELECT SUM(subtotal_amount), MONTH(FROM_UNIXTIME(create_timestamp)), YEAR(FROM_UNIXTIME(create_timestamp)) " +
            "FROM customer_order WHERE create_timestamp >= :sixMonthsAgo " +
            "GROUP BY MONTH(FROM_UNIXTIME(create_timestamp)), YEAR(FROM_UNIXTIME(create_timestamp)) " +
            "ORDER BY YEAR(FROM_UNIXTIME(create_timestamp)), MONTH(FROM_UNIXTIME(create_timestamp))", nativeQuery = true)
    List<Object[]> getRevenueForLastSixMonths(@Param("sixMonthsAgo") Long sixMonthsAgo);

    @Query("SELECT o.source, COUNT(o) FROM CustomerOrder o GROUP BY o.source")
    List<Object[]> getOrdersBySource();

    @Query("SELECT co FROM CustomerOrder co " +
            "JOIN FETCH co.listing l " +
            "JOIN FETCH co.buyer b " +
            "ORDER BY co.updatedTimestamp DESC")
    List<CustomerOrder> findTop10ByOrderByUpdatedTimestampDesc(Pageable pageable);
    @Query("SELECT MIN(o.createTimestamp) FROM CustomerOrder o")
    Long findMinCreateTimestamp();



    // Using native SQL query to get orders by year and month
    @Query(value = "SELECT * FROM customer_order o WHERE YEAR(FROM_UNIXTIME(o.create_timestamp)) = :year AND MONTH(FROM_UNIXTIME(o.create_timestamp)) = :month", nativeQuery = true)
    List<CustomerOrder> findOrdersByYearAndMonth(@Param("year") int year, @Param("month") int month);

    // Count orders by year and month using native SQL
    @Query(value = "SELECT COUNT(*) FROM customer_order o WHERE YEAR(FROM_UNIXTIME(o.create_timestamp)) = :year AND MONTH(FROM_UNIXTIME(o.create_timestamp)) = :month", nativeQuery = true)
    long countOrdersByYearAndMonth(@Param("year") int year, @Param("month") int month);

    CustomerOrder findFirstByOrderByCreateTimestampAsc();

}
