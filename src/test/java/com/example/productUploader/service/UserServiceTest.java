package com.example.productUploader.service;

import com.example.productUploader.model.User;
import com.example.productUploader.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    public UserServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveUser() {
        User user = new User();
        user.setPassword("plainPassword");

        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");

        userService.saveUser(user);

        assertTrue(user.getPassword().equals("encodedPassword"));
    }

    @Test
    public void testEmailExists() {
        when(userRepository.findByEmail("existing@example.com")).thenReturn(java.util.Optional.of(new User()));
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(java.util.Optional.empty());

        assertTrue(userService.emailExists("existing@example.com"));
        assertTrue(!userService.emailExists("nonexistent@example.com"));
    }
}
