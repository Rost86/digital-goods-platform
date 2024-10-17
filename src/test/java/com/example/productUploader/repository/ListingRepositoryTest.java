package com.example.productUploader.repository;

import com.example.productUploader.model.Listing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ListingRepositoryTest {

    @Autowired
    private ListingRepository listingRepository;

    @BeforeEach
    void setUp() {
        // Подготовка тестовых данных
        for (int i = 1; i <= 15; i++) {
            Listing listing = new Listing();
            listing.setViews(i * 10); // Устанавливаем количество просмотров
            listingRepository.save(listing);
        }
    }

    @Test
    void testFindTop10ByOrderByViewsDesc() {
        // Вызов метода репозитория
        List<Listing> top10Listings = listingRepository.findTop10ByOrderByViewsDesc();

        // Проверка результата
        assertThat(top10Listings).hasSize(10);
        assertThat(top10Listings.get(0).getViews()).isGreaterThan(top10Listings.get(9).getViews()); // Проверка порядка
    }
}
