package com.example.productUploader.controller;

import com.example.productUploader.model.User;
import com.example.productUploader.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Optional;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final UserService userService;

    public GlobalControllerAdvice(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("fullName")
    public String currentUserFullName() {
        // Retrieve the authenticated user from the SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return "Anonymous User";
        }

        String email = authentication.getName();
        Optional<User> userOptional = userService.findByEmail(email);

        return userOptional.map(user -> user.getUsername() + " " + user.getLastName()).orElse("Anonymous User");
    }

    @ModelAttribute("userId")
    public Long currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;  // Return null if the user is not authenticated
        }

        String email = authentication.getName();
        Optional<User> userOptional = userService.findByEmail(email);

        return userOptional.map(User::getId).orElse(null);  // Return user ID or null
    }
}
