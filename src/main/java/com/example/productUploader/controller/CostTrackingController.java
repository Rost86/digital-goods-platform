package com.example.productUploader.controller;

import com.example.productUploader.model.CostTracking;
import com.example.productUploader.model.User;
import com.example.productUploader.service.CostTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/costs")
public class CostTrackingController {

    private final CostTrackingService costTrackingService;

    @Autowired
    public CostTrackingController(CostTrackingService costTrackingService) {
        this.costTrackingService = costTrackingService;
    }

    /**
     * Show the cost management page for the logged-in user
     *
     * @param userId The ID of the logged-in user (provided by GlobalControllerAdvice)
     * @param model  The model to pass data to the view
     * @return The cost-management view
     */
    @GetMapping
    public String showCostManagementPage(@ModelAttribute("userId") Long userId, Model model) {
        List<CostTracking> costs = costTrackingService.getCostsByUserId(userId);
        model.addAttribute("costs", costs);
        return "cost-management";  // Return the cost management page (Thymeleaf template)
    }

    /**
     * Show the create cost form
     *
     * @param model The model to pass data to the view
     * @return The cost-create view
     */
    @GetMapping("/create")
    public String showCreateCostForm(Model model) {
        model.addAttribute("cost", new CostTracking());
        return "cost-create";  // Return the cost create page
    }

    /**
     * Process the create cost form
     *
     * @param userId       The ID of the logged-in user (provided by GlobalControllerAdvice)
     * @param costTracking The cost data to be saved
     * @return Redirect to the cost management page after saving
     */
    @PostMapping("/create")
    public String createCost(@ModelAttribute("userId") Long userId, @ModelAttribute CostTracking costTracking) {
        User user = new User();
        user.setId(userId);  // Set user based on the userId provided
        costTracking.setUser(user);  // Set the user to the cost tracking entity
        costTrackingService.saveOrUpdateCost(costTracking);
        return "redirect:/costs";  // Redirect to the cost management page after creation
    }

    /**
     * Show the edit cost form
     *
     * @param id    The ID of the cost to be edited
     * @param model The model to pass data to the view
     * @return The cost-edit view
     */
    @GetMapping("/edit/{id}")
    public String showEditCostForm(@PathVariable Long id, Model model) {
        CostTracking costTracking = costTrackingService.findById(id)
                .orElseThrow(() -> new RuntimeException("Cost entry not found with id: " + id));
        model.addAttribute("costTracking", costTracking);
        return "cost-edit";  // Return the cost edit page
    }

    /**
     * Process the edit cost form
     *
     * @param id           The ID of the cost entry to update
     * @param costTracking The updated cost data
     * @return Redirect to the cost management page after editing
     */
    @PostMapping("/edit/{id}")
    public String editCost(@PathVariable Long id, @ModelAttribute CostTracking costTracking) {
        CostTracking existingCost = costTrackingService.findById(id)
                .orElseThrow(() -> new RuntimeException("Cost entry not found with id: " + id));

        existingCost.setFixedCost(costTracking.getFixedCost());
        existingCost.setVariableCost(costTracking.getVariableCost());
        existingCost.setCostPerUnit(costTracking.getCostPerUnit());
        existingCost.setAdvertisingCost(costTracking.getAdvertisingCost());

        costTrackingService.saveOrUpdateCost(existingCost);
        return "redirect:/costs";  // Redirect to the cost management page after editing
    }

    /**
     * Show the delete cost confirmation page
     *
     * @param id    The ID of the cost entry to delete
     * @param model The model to pass data to the view
     * @return The cost-delete view
     */
    @GetMapping("/delete/{id}")
    public String showDeleteCostForm(@PathVariable Long id, Model model) {
        CostTracking costTracking = costTrackingService.findById(id)
                .orElseThrow(() -> new RuntimeException("Cost entry not found with id: " + id));
        model.addAttribute("costTracking", costTracking);
        return "cost-delete";  // Return the cost delete page
    }

    /**
     * Process the delete cost request
     *
     * @param id The ID of the cost entry to delete
     * @return Redirect to the cost management page after deletion
     */
    @PostMapping("/delete/{id}")
    public String deleteCost(@PathVariable Long id) {
        costTrackingService.deleteCost(id);
        return "redirect:/costs";  // Redirect to the cost management page after deletion
    }
}
