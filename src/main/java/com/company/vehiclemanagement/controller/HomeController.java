package com.company.vehiclemanagement.controller;

import com.company.vehiclemanagement.service.UserService;
import com.company.vehiclemanagement.service.VehicleService;
import com.company.vehiclemanagement.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private BookingService bookingService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Vehicle Management System");
        model.addAttribute("message", "Welcome to the Vehicle Management System");
        model.addAttribute("totalVehicles", vehicleService.getTotalVehicleCount());
        model.addAttribute("availableVehicles", vehicleService.getAvailableVehicleCount());
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            var user = userService.findByUsername(authentication.getName());
            if (user.isPresent()) {
                model.addAttribute("user", user.get());
                model.addAttribute("userBookings", bookingService.findBookingsByUser(user.get()));
                model.addAttribute("totalVehicles", vehicleService.getTotalVehicleCount());
                model.addAttribute("availableVehicles", vehicleService.getAvailableVehicleCount());
                model.addAttribute("userBookingCount", bookingService.getUserBookingCount(user.get()));

                // Admin-specific data
                if (user.get().getRole().name().equals("ADMIN")
                        || user.get().getRole().name().equals("FLEET_MANAGER")) {
                    model.addAttribute("pendingBookings", bookingService.findPendingBookings());
                    model.addAttribute("totalUsers", userService.getTotalUserCount());
                    return "admin/dashboard";
                }

                return "employee/dashboard";
            }
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String register() {
        return "auth/register";
    }
}