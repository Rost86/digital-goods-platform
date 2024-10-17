package com.example.productUploader.controller;

import com.example.productUploader.service.OrderAnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

@Controller
public class AnalyticsController {

    private final OrderAnalyticsService orderAnalyticsService;

    @Autowired
    public AnalyticsController(OrderAnalyticsService orderAnalyticsService) {
        this.orderAnalyticsService = orderAnalyticsService;
    }

    /**
     * Display business analytics page
     *
     * @param model Model to hold data for the view
     * @return analytics.html page
     */
    @GetMapping("/analytics")
    public String showAnalyticsPage(Model model) {
        // Get the list of months for ROI and Profit Margin calculation
        List<LocalDateTime> months = orderAnalyticsService.getAllMonthsFromFirstSale();

        // Format the months to "Month, Year" style (e.g., "December 2023")
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");

        // Prepare a map of formatted month names and their corresponding ROI and Profit Margin values
        Map<String, BigDecimal> roiByMonth = new LinkedHashMap<>();
        Map<String, BigDecimal> profitMarginByMonth = new LinkedHashMap<>();

        // Prepare chart labels and data for both ROI and Profit Margin
        StringBuilder chartLabels = new StringBuilder();
        StringBuilder chartDataROI = new StringBuilder();
        StringBuilder chartDataProfitMargin = new StringBuilder();

        for (LocalDateTime month : months) {
            String formattedMonth = month.format(formatter);

            // Calculate monthly ROI and Profit Margin
            BigDecimal monthROI = orderAnalyticsService.calculateMonthlyROI(month.getYear(), month.getMonthValue());
            BigDecimal monthProfitMargin = orderAnalyticsService.calculateMonthlyProfitMargin(month.getYear(), month.getMonthValue());

            roiByMonth.put(formattedMonth, monthROI);
            profitMarginByMonth.put(formattedMonth, monthProfitMargin);

            // Add month name to labels (formatted as 'Month Year')
            chartLabels.append(formattedMonth).append(",");

            // Append ROI and Profit Margin to their respective chart data
            chartDataROI.append(monthROI).append(",");
            chartDataProfitMargin.append(monthProfitMargin).append(",");
        }

        // Remove the trailing commas
        if (chartLabels.length() > 0) {
            chartLabels.setLength(chartLabels.length() - 1);
        }
        if (chartDataROI.length() > 0) {
            chartDataROI.setLength(chartDataROI.length() - 1);
        }
        if (chartDataProfitMargin.length() > 0) {
            chartDataProfitMargin.setLength(chartDataProfitMargin.length() - 1);
        }

        // Calculate totals and current month values
        BigDecimal totalROI = orderAnalyticsService.calculateTotalROI();
        BigDecimal totalProfitMargin = orderAnalyticsService.calculateTotalProfitMargin();
        int currentYear = LocalDateTime.now().getYear();
        int currentMonth = LocalDateTime.now().getMonthValue();
        BigDecimal currentMonthROI = orderAnalyticsService.calculateMonthlyROI(currentYear, currentMonth);
        BigDecimal currentMonthProfitMargin = orderAnalyticsService.calculateMonthlyProfitMargin(currentYear, currentMonth);

        // Add the calculated data to the model
        model.addAttribute("chartLabels", chartLabels.toString());
        model.addAttribute("chartData", chartDataROI.toString());  // For the ROI chart
        model.addAttribute("chartDataProfitMargin", chartDataProfitMargin.toString());  // For the Profit Margin chart

        // Add total and current month values
        model.addAttribute("totalROI", totalROI);
        model.addAttribute("totalProfitMargin", totalProfitMargin);
        model.addAttribute("currentMonthROI", currentMonthROI);
        model.addAttribute("currentMonthProfitMargin", currentMonthProfitMargin);

        // Add the month-wise ROI and Profit Margin values
        model.addAttribute("roiByMonth", roiByMonth);
        model.addAttribute("profitMarginByMonth", profitMarginByMonth);

        return "analytics";  // Return the Thymeleaf template 'analytics.html'
    }
}
