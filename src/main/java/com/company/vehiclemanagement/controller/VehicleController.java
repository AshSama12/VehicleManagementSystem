package com.company.vehiclemanagement.controller;

import com.company.vehiclemanagement.model.Vehicle;
import com.company.vehiclemanagement.model.VehicleType;
import com.company.vehiclemanagement.model.VehicleStatus;
import com.company.vehiclemanagement.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @GetMapping
    public String listVehicles(
            @RequestParam(required = false) VehicleType type,
            @RequestParam(required = false) VehicleStatus status,
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) String startDateTime,
            @RequestParam(required = false) String endDateTime,
            Model model) {

        try {
            if (startDateTime != null && endDateTime != null && !startDateTime.isEmpty() && !endDateTime.isEmpty()) {
                LocalDateTime start = LocalDateTime.parse(startDateTime);
                LocalDateTime end = LocalDateTime.parse(endDateTime);
                model.addAttribute("vehicles", vehicleService.findAvailableVehiclesBetween(start, end));
                model.addAttribute("filtered", true);
            } else if (type != null) {
                model.addAttribute("vehicles", vehicleService.findVehiclesByType(type));
                model.addAttribute("filtered", true);
            } else if (status != null) {
                model.addAttribute("vehicles", vehicleService.findVehiclesByStatus(status));
                model.addAttribute("filtered", true);
            } else if (minCapacity != null) {
                model.addAttribute("vehicles", vehicleService.findVehiclesByCapacity(minCapacity));
                model.addAttribute("filtered", true);
            } else {
                model.addAttribute("vehicles", vehicleService.findAvailableVehicles());
                model.addAttribute("filtered", false);
            }
        } catch (Exception e) {
            model.addAttribute("vehicles", vehicleService.findAvailableVehicles());
            model.addAttribute("error", "Invalid filter parameters");
        }

        model.addAttribute("vehicleTypes", VehicleType.values());
        model.addAttribute("vehicleStatuses", VehicleStatus.values());
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedCapacity", minCapacity);
        model.addAttribute("selectedStartDateTime", startDateTime);
        model.addAttribute("selectedEndDateTime", endDateTime);

        return "vehicles/list";
    }

    @GetMapping("/{id}")
    public String viewVehicle(@PathVariable Long id, Model model) {
        Optional<Vehicle> vehicle = vehicleService.findById(id);
        if (vehicle.isPresent()) {
            model.addAttribute("vehicle", vehicle.get());
            return "vehicles/detail";
        } else {
            return "redirect:/vehicles?error=Vehicle not found";
        }
    }

    @GetMapping("/new")
    public String newVehicleForm(Model model) {
        model.addAttribute("vehicle", new Vehicle());
        model.addAttribute("vehicleTypes", VehicleType.values());
        model.addAttribute("vehicleStatuses", VehicleStatus.values());
        return "vehicles/form";
    }

    @PostMapping
    public String createVehicle(@Valid @ModelAttribute Vehicle vehicle,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("vehicleTypes", VehicleType.values());
            model.addAttribute("vehicleStatuses", VehicleStatus.values());
            return "vehicles/form";
        }

        try {
            vehicleService.saveVehicle(vehicle);
            redirectAttributes.addFlashAttribute("success", "Vehicle created successfully!");
            return "redirect:/vehicles";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("vehicleTypes", VehicleType.values());
            model.addAttribute("vehicleStatuses", VehicleStatus.values());
            return "vehicles/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editVehicleForm(@PathVariable Long id, Model model) {
        Optional<Vehicle> vehicle = vehicleService.findById(id);
        if (vehicle.isPresent()) {
            model.addAttribute("vehicle", vehicle.get());
            model.addAttribute("vehicleTypes", VehicleType.values());
            model.addAttribute("vehicleStatuses", VehicleStatus.values());
            return "vehicles/form";
        } else {
            return "redirect:/vehicles?error=Vehicle not found";
        }
    }

    @PostMapping("/{id}")
    public String updateVehicle(@PathVariable Long id,
            @Valid @ModelAttribute Vehicle vehicle,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("vehicleTypes", VehicleType.values());
            model.addAttribute("vehicleStatuses", VehicleStatus.values());
            return "vehicles/form";
        }

        try {
            vehicle.setId(id);
            vehicleService.updateVehicle(vehicle);
            redirectAttributes.addFlashAttribute("success", "Vehicle updated successfully!");
            return "redirect:/vehicles/" + id;
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("vehicleTypes", VehicleType.values());
            model.addAttribute("vehicleStatuses", VehicleStatus.values());
            return "vehicles/form";
        }
    }

    @PostMapping("/{id}/status")
    public String updateVehicleStatus(@PathVariable Long id,
            @RequestParam VehicleStatus status,
            RedirectAttributes redirectAttributes) {
        try {
            vehicleService.updateVehicleStatus(id, status);
            redirectAttributes.addFlashAttribute("success", "Vehicle status updated successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/vehicles/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteVehicle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            vehicleService.deleteVehicle(id);
            redirectAttributes.addFlashAttribute("success", "Vehicle removed from service successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/vehicles";
    }

    // AJAX endpoints for dynamic filtering
    @GetMapping("/api/available")
    @ResponseBody
    public Object getAvailableVehicles(@RequestParam String startDateTime,
            @RequestParam String endDateTime) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDateTime);
            LocalDateTime end = LocalDateTime.parse(endDateTime);
            return vehicleService.findAvailableVehiclesBetween(start, end);
        } catch (Exception e) {
            return "{\"error\": \"Invalid date format\"}";
        }
    }

    @GetMapping("/api/check-license")
    @ResponseBody
    public Object checkLicensePlate(@RequestParam String licensePlate,
            @RequestParam(required = false) Long vehicleId) {
        boolean available;
        if (vehicleId != null) {
            available = vehicleService.isLicensePlateAvailableForUpdate(licensePlate, vehicleId);
        } else {
            available = vehicleService.isLicensePlateAvailable(licensePlate);
        }
        return "{\"available\": " + available + "}";
    }
}