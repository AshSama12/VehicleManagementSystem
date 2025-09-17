package com.company.vehiclemanagement.controller;

import com.company.vehiclemanagement.model.Booking;
import com.company.vehiclemanagement.model.BookingStatus;
import com.company.vehiclemanagement.model.User;
import com.company.vehiclemanagement.model.Vehicle;
import com.company.vehiclemanagement.service.BookingService;
import com.company.vehiclemanagement.service.UserService;
import com.company.vehiclemanagement.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private VehicleService vehicleService;

    @GetMapping
    public String listUserBookings(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            Optional<User> user = userService.findByUsername(authentication.getName());
            if (user.isPresent()) {
                model.addAttribute("bookings", bookingService.findBookingsByUser(user.get()));
                model.addAttribute("user", user.get());
                return "bookings/list";
            }
        }
        return "redirect:/login";
    }

    @GetMapping("/new")
    public String newBookingForm(@RequestParam(required = false) Long vehicleId,
            Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        Optional<User> user = userService.findByUsername(authentication.getName());
        if (!user.isPresent()) {
            return "redirect:/login";
        }

        Booking booking = new Booking();
        booking.setUser(user.get());

        if (vehicleId != null) {
            Optional<Vehicle> vehicle = vehicleService.findById(vehicleId);
            if (vehicle.isPresent()) {
                booking.setVehicle(vehicle.get());
            }
        }

        model.addAttribute("booking", booking);
        model.addAttribute("vehicles", vehicleService.findAvailableVehicles());
        return "bookings/form";
    }

    @PostMapping
    public String createBooking(@Valid @ModelAttribute Booking booking,
            BindingResult bindingResult,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        Optional<User> user = userService.findByUsername(authentication.getName());
        if (!user.isPresent()) {
            return "redirect:/login";
        }

        booking.setUser(user.get());

        if (bindingResult.hasErrors()) {
            model.addAttribute("vehicles", vehicleService.findAvailableVehicles());
            return "bookings/form";
        }

        try {
            bookingService.createBooking(booking);
            redirectAttributes.addFlashAttribute("success",
                    "Booking request submitted successfully! Please wait for approval.");
            return "redirect:/bookings";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("vehicles", vehicleService.findAvailableVehicles());
            return "bookings/form";
        }
    }

    @GetMapping("/{id}")
    public String viewBooking(@PathVariable Long id, Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        Optional<User> user = userService.findByUsername(authentication.getName());
        Optional<Booking> booking = bookingService.findById(id);

        if (!user.isPresent() || !booking.isPresent()) {
            return "redirect:/bookings?error=Booking not found";
        }

        // Users can only view their own bookings unless they're admin
        if (!booking.get().getUser().getId().equals(user.get().getId()) &&
                !user.get().getRole().name().equals("ADMIN") &&
                !user.get().getRole().name().equals("FLEET_MANAGER")) {
            return "redirect:/bookings?error=Access denied";
        }

        model.addAttribute("booking", booking.get());
        model.addAttribute("user", user.get());
        return "bookings/detail";
    }

    @GetMapping("/{id}/edit")
    public String editBookingForm(@PathVariable Long id, Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        Optional<User> user = userService.findByUsername(authentication.getName());
        Optional<Booking> booking = bookingService.findById(id);

        if (!user.isPresent() || !booking.isPresent()) {
            return "redirect:/bookings?error=Booking not found";
        }

        // Users can only edit their own pending bookings
        if (!booking.get().getUser().getId().equals(user.get().getId()) ||
                booking.get().getStatus() != BookingStatus.PENDING) {
            return "redirect:/bookings?error=Cannot edit this booking";
        }

        model.addAttribute("booking", booking.get());
        model.addAttribute("vehicles", vehicleService.findAvailableVehicles());
        return "bookings/form";
    }

    @PostMapping("/{id}")
    public String updateBooking(@PathVariable Long id,
            @Valid @ModelAttribute Booking booking,
            BindingResult bindingResult,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        Optional<User> user = userService.findByUsername(authentication.getName());
        if (!user.isPresent()) {
            return "redirect:/login";
        }

        booking.setId(id);
        booking.setUser(user.get());

        if (bindingResult.hasErrors()) {
            model.addAttribute("vehicles", vehicleService.findAvailableVehicles());
            return "bookings/form";
        }

        try {
            bookingService.updateBooking(booking);
            redirectAttributes.addFlashAttribute("success", "Booking updated successfully!");
            return "redirect:/bookings/" + id;
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("vehicles", vehicleService.findAvailableVehicles());
            return "bookings/form";
        }
    }

    @PostMapping("/{id}/cancel")
    public String cancelBooking(@PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        Optional<User> user = userService.findByUsername(authentication.getName());
        if (!user.isPresent()) {
            return "redirect:/login";
        }

        try {
            bookingService.cancelBooking(id, user.get());
            redirectAttributes.addFlashAttribute("success", "Booking cancelled successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/bookings";
    }

    // AJAX endpoints
    @GetMapping("/api/check-availability")
    @ResponseBody
    public Object checkVehicleAvailability(@RequestParam Long vehicleId,
            @RequestParam String startDateTime,
            @RequestParam String endDateTime,
            @RequestParam(required = false) Long bookingId) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDateTime);
            LocalDateTime end = LocalDateTime.parse(endDateTime);

            Optional<Vehicle> vehicle = vehicleService.findById(vehicleId);
            if (!vehicle.isPresent()) {
                return "{\"available\": false, \"message\": \"Vehicle not found\"}";
            }

            boolean hasConflicts = bookingService.hasConflictingBookings(vehicle.get(), start, end);

            // If checking for update, exclude current booking from conflicts
            if (bookingId != null) {
                // This would require more complex logic in the service
                // For now, we'll keep it simple
            }

            return "{\"available\": " + !hasConflicts + ", \"message\": \"" +
                    (hasConflicts ? "Vehicle is already booked for this time period" : "Vehicle is available") + "\"}";
        } catch (Exception e) {
            return "{\"available\": false, \"message\": \"Invalid date format\"}";
        }
    }
}