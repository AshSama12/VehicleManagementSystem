package com.company.vehiclemanagement.controller;

import com.company.vehiclemanagement.model.BookingStatus;
import com.company.vehiclemanagement.model.User;
import com.company.vehiclemanagement.model.Role;
import com.company.vehiclemanagement.service.BookingService;
import com.company.vehiclemanagement.service.UserService;
import com.company.vehiclemanagement.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN') or hasRole('FLEET_MANAGER')")
public class AdminController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private VehicleService vehicleService;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("pendingBookings", bookingService.findPendingBookings());
        model.addAttribute("totalUsers", userService.getTotalUserCount());
        model.addAttribute("activeUsers", userService.getActiveUserCount());
        model.addAttribute("totalVehicles", vehicleService.getTotalVehicleCount());
        model.addAttribute("availableVehicles", vehicleService.getAvailableVehicleCount());
        model.addAttribute("totalBookings", bookingService.getTotalBookingCount());
        model.addAttribute("pendingBookingsCount", bookingService.getBookingCountByStatus(BookingStatus.PENDING));
        return "admin/dashboard";
    }

    @GetMapping("/bookings")
    public String manageBookings(@RequestParam(required = false) BookingStatus status, Model model) {
        if (status != null) {
            model.addAttribute("bookings", bookingService.findBookingsByStatus(status));
            model.addAttribute("selectedStatus", status);
        } else {
            model.addAttribute("bookings", bookingService.findAllBookings());
        }
        model.addAttribute("bookingStatuses", BookingStatus.values());
        return "admin/bookings";
    }

    @GetMapping("/bookings/{id}")
    public String viewBookingForApproval(@PathVariable Long id, Model model) {
        Optional<com.company.vehiclemanagement.model.Booking> booking = bookingService.findById(id);
        if (booking.isPresent()) {
            model.addAttribute("booking", booking.get());
            return "admin/booking-detail";
        } else {
            return "redirect:/admin/bookings?error=Booking not found";
        }
    }

    @PostMapping("/bookings/{id}/approve")
    public String approveBooking(@PathVariable Long id,
            @RequestParam(required = false) String notes,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        Optional<User> approver = userService.findByUsername(authentication.getName());
        if (!approver.isPresent()) {
            return "redirect:/login";
        }

        try {
            bookingService.approveBooking(id, approver.get(), notes);
            redirectAttributes.addFlashAttribute("success", "Booking approved successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/bookings/" + id;
    }

    @PostMapping("/bookings/{id}/reject")
    public String rejectBooking(@PathVariable Long id,
            @RequestParam String notes,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        Optional<User> approver = userService.findByUsername(authentication.getName());
        if (!approver.isPresent()) {
            return "redirect:/login";
        }

        try {
            bookingService.rejectBooking(id, approver.get(), notes);
            redirectAttributes.addFlashAttribute("success", "Booking rejected successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/bookings/" + id;
    }

    @GetMapping("/users")
    public String manageUsers(@RequestParam(required = false) Role role, Model model) {
        if (role != null) {
            model.addAttribute("users", userService.findUsersByRole(role));
            model.addAttribute("selectedRole", role);
        } else {
            model.addAttribute("users", userService.findAllActiveUsers());
        }
        model.addAttribute("roles", Role.values());
        return "admin/users";
    }

    @PostMapping("/users/{id}/deactivate")
    public String deactivateUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deactivateUser(id);
            redirectAttributes.addFlashAttribute("success", "User deactivated successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/activate")
    public String activateUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.activateUser(id);
            redirectAttributes.addFlashAttribute("success", "User activated successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/role")
    public String updateUserRole(@PathVariable Long id,
            @RequestParam Role role,
            RedirectAttributes redirectAttributes) {
        try {
            userService.updateUserRole(id, role);
            redirectAttributes.addFlashAttribute("success", "User role updated successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/vehicles")
    public String manageVehicles(Model model) {
        model.addAttribute("vehicles", vehicleService.findAllVehicles());
        model.addAttribute("needingService", vehicleService.findVehiclesNeedingService());
        model.addAttribute("expiringInsurance", vehicleService.findVehiclesWithExpiringInsurance(30));
        return "admin/vehicles";
    }

    @GetMapping("/reports")
    public String viewReports(Model model) {
        // Add reporting data
        model.addAttribute("totalBookings", bookingService.getTotalBookingCount());
        model.addAttribute("approvedBookings", bookingService.getBookingCountByStatus(BookingStatus.APPROVED));
        model.addAttribute("rejectedBookings", bookingService.getBookingCountByStatus(BookingStatus.REJECTED));
        model.addAttribute("completedBookings", bookingService.getBookingCountByStatus(BookingStatus.COMPLETED));
        model.addAttribute("totalUsers", userService.getTotalUserCount());
        model.addAttribute("activeUsers", userService.getActiveUserCount());
        model.addAttribute("totalVehicles", vehicleService.getTotalVehicleCount());
        model.addAttribute("availableVehicles", vehicleService.getAvailableVehicleCount());
        return "admin/reports";
    }

    // AJAX endpoints for admin operations
    @PostMapping("/api/bookings/{id}/complete")
    @ResponseBody
    public Object completeBooking(@PathVariable Long id) {
        try {
            bookingService.completeBooking(id);
            return "{\"success\": true, \"message\": \"Booking completed successfully\"}";
        } catch (RuntimeException e) {
            return "{\"success\": false, \"message\": \"" + e.getMessage() + "\"}";
        }
    }

    @GetMapping("/api/stats")
    @ResponseBody
    public Object getStats() {
        return String.format("{" +
                "\"totalBookings\": %d," +
                "\"pendingBookings\": %d," +
                "\"totalVehicles\": %d," +
                "\"availableVehicles\": %d," +
                "\"totalUsers\": %d," +
                "\"activeUsers\": %d" +
                "}",
                bookingService.getTotalBookingCount(),
                bookingService.getBookingCountByStatus(BookingStatus.PENDING),
                vehicleService.getTotalVehicleCount(),
                vehicleService.getAvailableVehicleCount(),
                userService.getTotalUserCount(),
                userService.getActiveUserCount());
    }
}