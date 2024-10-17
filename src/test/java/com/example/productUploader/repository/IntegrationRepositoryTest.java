package com.example.productUploader.repository;

import com.example.productUploader.model.Integration;
import com.example.productUploader.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class IntegrationRepositoryTest {

    @Autowired
    private IntegrationRepository integrationRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Integration integration;

    @BeforeEach
    void setUp() {
        // Создание тестового пользователя
        user = new User();
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        userRepository.save(user);

        // Создание тестовой интеграции
        integration = new Integration();
        integration.setUser(user);
        integrationRepository.save(integration);
    }

    @Test
    void testFindByUserId() {
        List<Integration> integrations = integrationRepository.findByUserId(user.getId());
        assertThat(integrations).isNotEmpty();
        assertThat(integrations.get(0).getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    void testFindAllByUser() {
        List<Integration> integrations = integrationRepository.findAllByUser(user);
        assertThat(integrations).isNotEmpty();
        assertThat(integrations.get(0).getUser()).isEqualTo(user);
    }
}
