package com.example.productUploader.repository;

import com.example.productUploader.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setUsername("TestUser");
        user.setPassword("ValidPassword123");

        User savedUser = userRepository.save(user);


        assertNotNull(savedUser.getId());
        assertNotNull(userRepository.findByEmail("test@example.com"));
    }
}
