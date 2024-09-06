package com.example.productUploader.controller;

import com.example.productUploader.model.User;
import com.example.productUploader.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class RegistrationControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Model model;

    @InjectMocks
    private RegistrationController registrationController;

    public RegistrationControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSuccessfulRegistration() {
        User user = new User();
        user.setEmail("test@example.com");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.emailExists(user.getEmail())).thenReturn(false);

        String view = registrationController.registerUser(user, bindingResult, model);

        assertEquals("redirect:/login", view);
    }

    @Test
    public void testRegistrationWithValidationErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);

        User user = new User();
        String view = registrationController.registerUser(user, bindingResult, model);

        assertEquals("register", view);
    }

    @Test
    public void testRegistrationWithExistingEmail() {
        User user = new User();
        user.setEmail("test@example.com");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.emailExists(user.getEmail())).thenReturn(true);

        String view = registrationController.registerUser(user, bindingResult, model);

        assertEquals("register", view);
    }
}
