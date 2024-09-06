package com.example.productUploader.service;

import com.example.productUploader.model.User;
import com.example.productUploader.model.Role;
import com.example.productUploader.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    public CustomUserDetailsServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoadUserByUsername_Success() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRole(Role.USER);  // Обязательно назначаем роль

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        customUserDetailsService.loadUserByUsername("test@example.com");
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("nonexistent@example.com");
        });
    }
}
