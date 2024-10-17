package com.example.productUploader.repository;

import com.example.productUploader.model.CostTracking;
import com.example.productUploader.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CostTrackingRepositoryTest {

    @Autowired
    private CostTrackingRepository costTrackingRepository;

    private User user;
    private CostTracking costTracking;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("TestUser"); // Устанавливаем имя пользователя

        costTracking = new CostTracking();
        costTracking.setId(1L);
        costTracking.setUser(user);
        costTracking.setYear(2023);
        costTracking.setMonth(10);

        // Сохраняем данные для тестирования
        costTrackingRepository.save(costTracking);
    }

    @Test
    void testFindByUser() {
        List<CostTracking> results = costTrackingRepository.findByUser(user);
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getUser().getUsername()).isEqualTo("TestUser");
    }

    @Test
    void testFindByUserId() {
        List<CostTracking> results = costTrackingRepository.findByUserId(1L);
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getUser().getId()).isEqualTo(1L);
    }

    @Test
    void testFindByYearAndMonth() {
        Optional<CostTracking> result = costTrackingRepository.findByYearAndMonth(2023, 10);
        assertThat(result).isPresent();
        assertThat(result.get().getYear()).isEqualTo(2023);
        assertThat(result.get().getMonth()).isEqualTo(10);
    }

    @Test
    void testFindFirstByOrderByIdAsc() {
        Optional<CostTracking> result = costTrackingRepository.findFirstByOrderByIdAsc();
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L); // assuming it's the first entry
    }
}
