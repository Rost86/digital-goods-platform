package com.example.productUploader.service;

import com.example.productUploader.model.CostTracking;
import com.example.productUploader.model.CustomerOrder;
import com.example.productUploader.repository.CustomerOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderAnalyticsService {

    @Autowired
    private CustomerOrderRepository customerOrderRepository;

    @Autowired
    private CostTrackingService costTrackingService;
    public BigDecimal calculateMonthlyProfitMargin(int year, int month) {
        // Get total revenue for the month (divide by 100 to convert from stored cents value)
        BigDecimal totalRevenue = calculateRevenueForMonth(year, month).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);

        // Get total costs (fixed + variable) for the month
        BigDecimal totalCosts = getCostsForMonth(year, month);

        // If there is no revenue, profit margin cannot be calculated
        if (totalRevenue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        // Calculate profit (total revenue - total costs)
        BigDecimal profit = totalRevenue.subtract(totalCosts);

        // Calculate Profit Margin: (Profit / Revenue) * 100
        return profit.divide(totalRevenue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
    }

    /**
     * Calculate the total Profit Margin for all months from the first sale.
     *
     * @return Total Profit Margin as a percentage.
     */
    public BigDecimal calculateTotalProfitMargin() {
        List<LocalDateTime> allMonths = getAllMonthsFromFirstSale();
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalCosts = BigDecimal.ZERO;

        for (LocalDateTime month : allMonths) {
            // Divide by 100 for revenue since it's stored in cents
            totalRevenue = totalRevenue.add(calculateRevenueForMonth(month.getYear(), month.getMonthValue()).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP));
            totalCosts = totalCosts.add(getCostsForMonth(month.getYear(), month.getMonthValue()));
        }

        // If there is no revenue, profit margin cannot be calculated
        if (totalRevenue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        // Calculate total profit
        BigDecimal totalProfit = totalRevenue.subtract(totalCosts);

        // Calculate and return total Profit Margin
        return totalProfit.divide(totalRevenue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
    }
    /**
     * Calculate monthly ROI (Return on Investment) for a specific month.
     * Revenue is divided by 100 as it is stored multiplied by 100 (in cents).
     * Costs remain as they are since they are stored without multiplying.
     *
     * @param year  The year to calculate ROI for.
     * @param month The month to calculate ROI for.
     * @return Monthly ROI as a percentage.
     */
    public BigDecimal calculateMonthlyROI(int year, int month) {
        // Get total revenue for the month (divide by 100 to convert from stored cents value)
        BigDecimal totalRevenue = calculateRevenueForMonth(year, month).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);

        // Get total costs (fixed + variable) for the month
        BigDecimal totalCosts = getCostsForMonth(year, month);

        // If there are no costs, ROI cannot be calculated
        if (totalCosts.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        // Calculate profit (total revenue - total costs)
        BigDecimal profit = totalRevenue.subtract(totalCosts);

        // Calculate ROI: (Profit / Costs) * 100
        return profit.divide(totalCosts, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
    }

    /**
     * Calculate the total revenue for a specific month.
     * Revenue is stored in cents, so divide by 100 for calculations.
     *
     * @param year  The year of the revenue.
     * @param month The month of the revenue.
     * @return Total revenue for the specified month.
     */
    private BigDecimal calculateRevenueForMonth(int year, int month) {
        List<CustomerOrder> orders = customerOrderRepository.findOrdersByYearAndMonth(year, month);
        return orders.stream()
                .map(order -> BigDecimal.valueOf(order.getGrandTotal().getAmount()))  // Convert Integer (cents) to BigDecimal
                .reduce(BigDecimal.ZERO, BigDecimal::add);  // Sum up total revenue in cents
    }

    /**
     * Get the total costs (fixed + variable) for a specific month.
     * If no costs are found for the month, find the closest previous month with costs.
     *
     * @param year  The year of the costs.
     * @param month The month of the costs.
     * @return Total costs for the month.
     */
    private BigDecimal getCostsForMonth(int year, int month) {
        Optional<CostTracking> costTracking = costTrackingService.getCostByYearAndMonth(year, month);

        // If no costs for the month, find the closest previous month with costs
        return costTracking
                .map(cost -> cost.getFixedCost().add(cost.getVariableCost()))  // Return total costs for the month
                .orElseGet(() -> findClosestCosts(year, month));
    }

    /**
     * Find the closest previous month with costs if no costs exist for the given month.
     *
     * @param year  The year of the costs.
     * @param month The month of the costs.
     * @return Total costs from the closest previous month.
     */
    private BigDecimal findClosestCosts(int year, int month) {
        // Try to find the cost for the exact year and month
        Optional<CostTracking> costTracking = costTrackingService.getCostByYearAndMonth(year, month);

        if (costTracking.isPresent()) {
            return costTracking.get().getFixedCost().add(costTracking.get().getVariableCost());
        }

        // If no cost is found for the specific month/year, get the first entry with the smallest ID
        Optional<CostTracking> firstCostTracking = costTrackingService.getFirstCostEntry();

        if (firstCostTracking.isPresent()) {
            return firstCostTracking.get().getFixedCost().add(firstCostTracking.get().getVariableCost());
        }

        // Return 0 if no costs are found in the database at all
        return BigDecimal.ZERO;
    }

    /**
     * Calculate the total ROI (Return on Investment) for all months from the first sale.
     *
     * @return Total ROI as a percentage.
     */
    public BigDecimal calculateTotalROI() {
        List<LocalDateTime> allMonths = getAllMonthsFromFirstSale();
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalCosts = BigDecimal.ZERO;

        for (LocalDateTime month : allMonths) {
            // Divide by 100 for revenue since it's stored in cents
            totalRevenue = totalRevenue.add(calculateRevenueForMonth(month.getYear(), month.getMonthValue()).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP));
            totalCosts = totalCosts.add(getCostsForMonth(month.getYear(), month.getMonthValue()));
        }

        // If there are no costs, ROI cannot be calculated
        if (totalCosts.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        // Calculate total profit
        BigDecimal totalProfit = totalRevenue.subtract(totalCosts);

        // Calculate and return total ROI
        return totalProfit.divide(totalCosts, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
    }

    /**
     * Retrieve all months from the date of the first sale to the current month.
     *
     * @return A list of LocalDateTime objects representing each month from the first sale to the current month.
     */
    public List<LocalDateTime> getAllMonthsFromFirstSale() {
        // Get the first order
        CustomerOrder firstOrder = customerOrderRepository.findFirstByOrderByCreateTimestampAsc();

        if (firstOrder == null) {
            return new ArrayList<>();  // Return an empty list if no orders exist
        }

        LocalDateTime start = LocalDateTime.ofEpochSecond(firstOrder.getCreateTimestamp(), 0, ZoneOffset.UTC);
        LocalDateTime now = LocalDateTime.now();

        List<LocalDateTime> months = new ArrayList<>();
        while (!start.isAfter(now)) {  // Stop when start exceeds the current date
            months.add(start);
            start = start.plusMonths(1);  // Iterate through the months
        }
        return months;
    }

    public void displayFormattedMonths(List<LocalDateTime> months) {
        // Define the DateTimeFormatter for "Month, Year" format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");

        // Loop through the list of months and format each date
        for (LocalDateTime month : months) {
            String formattedDate = month.format(formatter);  // Format the month to "Month, Year"
            System.out.println(formattedDate);  // Output the formatted date
        }
    }
}
