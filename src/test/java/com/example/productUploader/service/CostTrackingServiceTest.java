package com.example.productUploader.service;

import com.example.productUploader.model.CostTracking;
import com.example.productUploader.model.User;
import com.example.productUploader.repository.CostTrackingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

class CostTrackingServiceTest {

    @Mock
    private CostTrackingRepository costTrackingRepository;

    @InjectMocks
    private CostTrackingService costTrackingService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Создаем тестового пользователя
        user = new User();
        user.setId(1L);
    }

    @Test
    void testGetCostsByUser() {
        // Мокаем поведение репозитория
        when(costTrackingRepository.findByUser(user)).thenReturn(List.of(new CostTracking()));

        // Тестируем метод сервиса
        List<CostTracking> costs = costTrackingService.getCostsByUser(user);
        assertFalse(costs.isEmpty());
    }
}
