package com.example.productUploader.controller;

import com.example.productUploader.model.Listing;
import com.example.productUploader.model.CustomerOrder;
import com.example.productUploader.repository.CustomerOrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import com.example.productUploader.repository.ListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.text.SimpleDateFormat;

@Controller
public class IndexController {

    @Autowired
    private CustomerOrderRepository customerOrderRepository;

    @Autowired
    private ListingRepository listingRepository;

    @GetMapping("/index")
    public String showIndex(Model model) {
        // Get total revenue for all time, dividing by 100 to format as decimal
        BigDecimal totalRevenue = customerOrderRepository.getTotalRevenue();
        BigDecimal formattedTotalRevenue = (totalRevenue != null) ? totalRevenue.divide(new BigDecimal(100)) : BigDecimal.ZERO;

        // Calculate timestamp for one month ago and fetch revenue for the last month
        LocalDate oneMonthAgo = LocalDate.now().minus(1, ChronoUnit.MONTHS);
        long startDateTimestamp = oneMonthAgo.atStartOfDay().toEpochSecond(ZoneOffset.UTC);
        BigDecimal lastMonthRevenue = customerOrderRepository.getTotalRevenueForLastMonth(startDateTimestamp);
        BigDecimal formattedLastMonthRevenue = (lastMonthRevenue != null) ? lastMonthRevenue.divide(new BigDecimal(100)) : BigDecimal.ZERO;

        // Get total number of orders for all time
        Long totalOrders = customerOrderRepository.getTotalOrders();
        totalOrders = (totalOrders != null) ? totalOrders : 0L;

        // Get total number of orders for the last month
        Long lastMonthOrders = customerOrderRepository.getTotalOrdersForLastMonth(startDateTimestamp);
        lastMonthOrders = (lastMonthOrders != null) ? lastMonthOrders : 0L;

        // Calculate the timestamp for six months ago
        LocalDateTime sixMonthsAgoDate = LocalDateTime.now().minus(6, ChronoUnit.MONTHS);
        long sixMonthsAgo = sixMonthsAgoDate.toEpochSecond(ZoneOffset.UTC); // Convert LocalDateTime to Unix timestamp

        // Fetch the revenue data and createTimestamp from the repository
        List<Object[]> revenueData = customerOrderRepository.getRevenueForLastSixMonths(sixMonthsAgo);

        // Prepare the data for chart labels and values
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        Map<String, BigDecimal> monthlyRevenueMap = new TreeMap<>(); // TreeMap to keep months in order

        for (Object[] row : revenueData) {
            BigDecimal revenue = (BigDecimal) row[0];
            if (row[1] != null && row[2] != null) {  // Check if month and year are not null
                int month = ((Number) row[1]).intValue();
                int year = ((Number) row[2]).intValue();
                String monthLabel = String.format("%02d/%d", month, year);  // Format as MM/YYYY
                monthlyRevenueMap.put(monthLabel, revenue.divide(new BigDecimal(100))); // Divide by 100 for proper format
            }
        }

        // Convert map to JSON-like format for frontend
        String chartLabels = String.join(",", monthlyRevenueMap.keySet());
        String chartData = monthlyRevenueMap.values().stream()
                .map(BigDecimal::toString)
                .reduce((a, b) -> a + "," + b)
                .orElse("");

        // Get orders count by source (e.g., Etsy, Direct, etc.)
        List<Object[]> ordersBySource = customerOrderRepository.getOrdersBySource();

        // Prepare data for pie chart
        Map<String, Long> orderSourceMap = ordersBySource.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0], // Source name
                        row -> (Long) row[1]    // Count of orders
                ));

        // Calculate total orders and percentages for each source
        long totalOrderCount = orderSourceMap.values().stream().mapToLong(Long::longValue).sum();
        Map<String, Double> sourcePercentages = orderSourceMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> (entry.getValue() * 100.0) / totalOrderCount
                ));

        // Prepare chart labels and data
        String pieLabels = String.join(",", sourcePercentages.keySet());
        String pieData = sourcePercentages.values().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        // Add top 10 listings
        List<Listing> popularListings = listingRepository.findTop10ByOrderByViewsDesc();

        // Get top 10 latest orders
        PageRequest pageRequest = PageRequest.of(0, 10);  // Страница 0, ограничение 10 записей
        List<CustomerOrder> latestOrders = customerOrderRepository.findTop10ByOrderByUpdatedTimestampDesc(pageRequest);

        // Fetch all orders
        List<CustomerOrder> allOrders = customerOrderRepository.findAll();

        // Calculate sales per country
        Map<String, Map<String, String>> countrySalesMap = new HashMap<>();
        for (CustomerOrder order : allOrders) {
            String country = order.getCountryIso();
            BigDecimal subtotal = new BigDecimal(order.getSubtotal().getAmount()).divide(new BigDecimal(100)); // Convert to BigDecimal and divide by 100
            String subtotalFormatted = "$" + subtotal.toString(); // Format with currency

            countrySalesMap.putIfAbsent(country.toLowerCase(), new HashMap<>());
            Map<String, String> countryData = countrySalesMap.get(country.toLowerCase());

            // Add or update country sales data
            int totalOrdersByCountry = Integer.parseInt(countryData.getOrDefault("total", "0")) + 1;  // Rename to avoid conflict
            BigDecimal totalAmount = new BigDecimal(countryData.getOrDefault("amount", "0").replace("$", ""))
                    .add(subtotal);  // Sum the amounts

            countryData.put("total", String.valueOf(totalOrdersByCountry));
            countryData.put("amount", "$" + totalAmount.toString());
        }

        // Convert countrySalesMap to JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        String countrySalesMapJson;
        try {
            countrySalesMapJson = objectMapper.writeValueAsString(countrySalesMap);
        } catch (JsonProcessingException e) {
            e.printStackTrace();  // Log the error for debugging
            countrySalesMapJson = "{}";  // Fallback to an empty JSON object if an error occurs
        }


        // Add formatted values to the model
        model.addAttribute("latestOrders", latestOrders);
        model.addAttribute("popularListings", popularListings);
        model.addAttribute("totalRevenue", formattedTotalRevenue);
        model.addAttribute("lastMonthRevenue", formattedLastMonthRevenue);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("lastMonthOrders", lastMonthOrders);
        model.addAttribute("chartLabels", chartLabels);
        model.addAttribute("chartData", chartData);
        model.addAttribute("pieLabels", pieLabels);
        model.addAttribute("pieData", pieData);
        model.addAttribute("countrySalesMapJson", countrySalesMapJson);  // JSON string for Thymeleaf to directly use

        return "index";
    }

    public String formatTimestamp(Long timestamp) {
        if (timestamp == null) {
            return "N/A";
        }
        Date date = new Date(timestamp * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(date);
    }

    public String getCountryNameByIso(String isoCode) {
        if (isoCode == null || isoCode.isEmpty()) {
            return "Unknown Country";
        }
        Locale locale = new Locale("", isoCode);
        return locale.getDisplayCountry();
    }
}
