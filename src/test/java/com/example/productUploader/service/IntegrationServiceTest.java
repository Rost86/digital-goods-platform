package com.example.productUploader.service;

import com.example.productUploader.model.Integration;
import com.example.productUploader.repository.IntegrationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

class IntegrationServiceTest {

    @Mock
    private IntegrationRepository integrationRepository;

    @InjectMocks
    private IntegrationService integrationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetIntegrationsByUser() {
        Long userId = 1L;

        // Мокаем поведение репозитория
        when(integrationRepository.findByUserId(userId)).thenReturn(List.of(new Integration()));

        // Тестируем метод сервиса
        List<Integration> integrations = integrationService.getIntegrationsByUser(userId);
        assertFalse(integrations.isEmpty());
    }
}
