package com.example.productUploader.controller;

import com.example.productUploader.model.Integration;
import com.example.productUploader.model.User;
import com.example.productUploader.service.IntegrationService;
import com.example.productUploader.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class UserController {

    private final UserService userService;
    private final IntegrationService integrationService;

    public UserController(UserService userService, IntegrationService integrationService) {
        this.userService = userService;
        this.integrationService = integrationService;
    }

    // Display the user's profile and their integrations
    @GetMapping("/profile")
    public String showUserProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userService.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        model.addAttribute("user", user);
        return "profile";
    }

    // Update the user's profile
    @PostMapping("/profile")
    public String updateUserProfile(User userForm, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // Find the current user
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Update user details
        user.setUsername(userForm.getUsername());
        user.setLastName(userForm.getLastName());
        user.setEmail(userForm.getEmail());
        user.setShopName(userForm.getShopName());
        user.setLocation(userForm.getLocation());
        user.setPhoneNumber(userForm.getPhoneNumber());
        user.setBirthday(userForm.getBirthday());

        // Save updated user
        userService.updateUser(user);

        model.addAttribute("user", user);
        model.addAttribute("successMessage", "Profile updated successfully");
        return "profile";
    }
    // Edit an existing integrations
    @GetMapping("/profile/integration/edit")
    public String editIntegrations(Model model) {
        // Add the list of integrations to the model for the sidebar navigation
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<Integration> integrations = integrationService.findAllByUser(user);

        model.addAttribute("integration", new Integration());
        model.addAttribute("integrations", integrations); // Add integrations to the model
        return "edit-integrations";
    }
    // Edit an existing integration
    @GetMapping("/profile/integration/edit/{id}")
    public String editIntegration(@PathVariable("id") Long id, Model model) {
        Integration integration = integrationService.getIntegrationById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid integration ID"));

        model.addAttribute("integration", integration);
        return "edit-integration";
    }

    // Add a new integration
    @GetMapping("/profile/integration/add")
    public String addIntegration(Model model) {

        model.addAttribute("integration", new Integration());
        return "add-integration";
    }

    @PostMapping("/profile/integration/add")
    public String saveNewIntegration(@ModelAttribute("integration") Integration integration) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        integration.setUser(user);
        integrationService.save(integration);

        return "redirect:/profile/integration/edit";
    }

    @PostMapping("/profile/integration/update")
    public String updateIntegration(@ModelAttribute("integration") Integration integration) {
        integrationService.save(integration);
        return "redirect:/profile/integration/edit";
    }

    @PostMapping("/profile/integration/delete/{id}")
    public String deleteIntegration(@PathVariable("id") Long id) {
        integrationService.deleteIntegration(id);
        return "redirect:/profile/integration/edit";  // Redirect back to the profile page
    }
}
