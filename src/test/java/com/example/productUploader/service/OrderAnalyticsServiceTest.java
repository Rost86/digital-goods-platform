package com.example.productUploader.service;

import com.example.productUploader.model.CostTracking;
import com.example.productUploader.model.CustomerOrder;
import com.example.productUploader.model.OrderAmount;
import com.example.productUploader.repository.CustomerOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class OrderAnalyticsServiceTest {

    @Mock
    private CustomerOrderRepository customerOrderRepository;

    @Mock
    private CostTrackingService costTrackingService;

    @InjectMocks
    private OrderAnalyticsService orderAnalyticsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @ParameterizedTest
    @CsvSource({
            "2023, 10, 50000, 200, 100, 40.00",
            "2023, 11, 100000, 500, 300, 20.00",
            "2023, 12, 30000, 100, 50, 50.00"
    })
    void testCalculateMonthlyProfitMarginParameterized(int year, int month, int grandTotalAmount, int fixedCost, int variableCost, String expectedProfitMargin) {
        CustomerOrder order = new CustomerOrder();
        OrderAmount orderAmount = new OrderAmount();
        orderAmount.setAmount(grandTotalAmount);
        order.setGrandTotal(orderAmount);
        when(customerOrderRepository.findOrdersByYearAndMonth(year, month)).thenReturn(List.of(order));

        CostTracking costTracking = new CostTracking();
        costTracking.setFixedCost(BigDecimal.valueOf(fixedCost));
        costTracking.setVariableCost(BigDecimal.valueOf(variableCost));
        when(costTrackingService.getCostByYearAndMonth(year, month)).thenReturn(Optional.of(costTracking));

        BigDecimal profitMargin = orderAnalyticsService.calculateMonthlyProfitMargin(year, month);

        BigDecimal roundedProfitMargin = profitMargin.setScale(2, RoundingMode.HALF_UP);

        assertEquals(new BigDecimal(expectedProfitMargin), roundedProfitMargin);
    }

}
