package com.example.productUploader.repository;

import com.example.productUploader.model.CustomerOrder;
import com.example.productUploader.model.OrderAmount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomerOrderRepositoryTest {

    @Autowired
    private CustomerOrderRepository customerOrderRepository;

    private CustomerOrder order;

    @BeforeEach
    void setUp() {
        order = new CustomerOrder();
        order.setCreateTimestamp(System.currentTimeMillis() / 1000L); // Unix timestamp
        order.setSubtotal(new OrderAmount(100, "USD")); // Используем Integer для amount
        customerOrderRepository.save(order);
    }

    @Test
    void testGetTotalRevenue() {
        BigDecimal totalRevenue = customerOrderRepository.getTotalRevenue();
        assertThat(totalRevenue).isNotNull();
        assertThat(totalRevenue).isEqualTo(BigDecimal.valueOf(100.0));
    }

    @Test
    void testGetTotalRevenueForLastMonth() {
        Long lastMonthTimestamp = System.currentTimeMillis() / 1000L - (30 * 24 * 60 * 60); // 30 дней назад
        BigDecimal totalRevenueLastMonth = customerOrderRepository.getTotalRevenueForLastMonth(lastMonthTimestamp);
        assertThat(totalRevenueLastMonth).isNotNull();
    }

    @Test
    void testGetTotalOrders() {
        Long totalOrders = customerOrderRepository.getTotalOrders();
        assertThat(totalOrders).isNotNull();
        assertThat(totalOrders).isEqualTo(1L);
    }

    @Test
    void testGetTotalOrdersForLastMonth() {
        Long lastMonthTimestamp = System.currentTimeMillis() / 1000L - (30 * 24 * 60 * 60); // 30 дней назад
        Long totalOrdersLastMonth = customerOrderRepository.getTotalOrdersForLastMonth(lastMonthTimestamp);
        assertThat(totalOrdersLastMonth).isNotNull();
    }

    @Test
    void testGetRevenueForLastSixMonths() {
        Long sixMonthsAgo = System.currentTimeMillis() / 1000L - (6 * 30 * 24 * 60 * 60); // 6 месяцев назад
        List<Object[]> revenueForLastSixMonths = customerOrderRepository.getRevenueForLastSixMonths(sixMonthsAgo);
        assertThat(revenueForLastSixMonths).isNotNull();
    }

    @Test
    void testGetOrdersBySource() {
        List<Object[]> ordersBySource = customerOrderRepository.getOrdersBySource();
        assertThat(ordersBySource).isNotNull();
    }

    @Test
    void testFindTop10ByOrderByUpdatedTimestampDesc() {
        List<CustomerOrder> top10Orders = customerOrderRepository.findTop10ByOrderByUpdatedTimestampDesc(PageRequest.of(0, 10));
        assertThat(top10Orders).isNotEmpty();
    }

    @Test
    void testFindMinCreateTimestamp() {
        Long minCreateTimestamp = customerOrderRepository.findMinCreateTimestamp();
        assertThat(minCreateTimestamp).isNotNull();
    }

    @Test
    void testFindOrdersByYearAndMonth() {
        List<CustomerOrder> ordersByYearAndMonth = customerOrderRepository.findOrdersByYearAndMonth(2023, 10);
        assertThat(ordersByYearAndMonth).isNotNull();
    }

    @Test
    void testCountOrdersByYearAndMonth() {
        long countOrders = customerOrderRepository.countOrdersByYearAndMonth(2023, 10);
        assertThat(countOrders).isGreaterThanOrEqualTo(0);
    }

    @Test
    void testFindFirstByOrderByCreateTimestampAsc() {
        CustomerOrder firstOrder = customerOrderRepository.findFirstByOrderByCreateTimestampAsc();
        assertThat(firstOrder).isNotNull();
    }
}
