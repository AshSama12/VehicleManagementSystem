package com.company.vehiclemanagement.service;

import com.company.vehiclemanagement.model.Vehicle;
import com.company.vehiclemanagement.model.VehicleStatus;
import com.company.vehiclemanagement.model.VehicleType;
import com.company.vehiclemanagement.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    public List<Vehicle> findAllVehicles() {
        return vehicleRepository.findAll();
    }

    public List<Vehicle> findAvailableVehicles() {
        return vehicleRepository.findByStatusOrderByMakeAscModelAsc(VehicleStatus.AVAILABLE);
    }

    public List<Vehicle> findAvailableVehiclesBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return vehicleRepository.findAvailableVehiclesBetween(startDateTime, endDateTime);
    }

    public List<Vehicle> findVehiclesByType(VehicleType type) {
        return vehicleRepository.findByType(type);
    }

    public List<Vehicle> findVehiclesByStatus(VehicleStatus status) {
        return vehicleRepository.findByStatus(status);
    }

    public List<Vehicle> findVehiclesByCapacity(Integer minCapacity) {
        return vehicleRepository.findBySeatingCapacityGreaterThanEqual(minCapacity);
    }

    public Optional<Vehicle> findById(Long id) {
        return vehicleRepository.findById(id);
    }

    public Optional<Vehicle> findByLicensePlate(String licensePlate) {
        return Optional.ofNullable(vehicleRepository.findByLicensePlate(licensePlate));
    }

    public Vehicle saveVehicle(Vehicle vehicle) {
        // Check if license plate already exists
        if (vehicle.getId() == null && vehicleRepository.existsByLicensePlate(vehicle.getLicensePlate())) {
            throw new RuntimeException("Vehicle with license plate already exists: " + vehicle.getLicensePlate());
        }

        // Set default status if not provided
        if (vehicle.getStatus() == null) {
            vehicle.setStatus(VehicleStatus.AVAILABLE);
        }

        return vehicleRepository.save(vehicle);
    }

    public Vehicle updateVehicle(Vehicle vehicle) {
        Vehicle existingVehicle = vehicleRepository.findById(vehicle.getId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found: " + vehicle.getId()));

        // Update vehicle details
        existingVehicle.setMake(vehicle.getMake());
        existingVehicle.setModel(vehicle.getModel());
        existingVehicle.setYear(vehicle.getYear());
        existingVehicle.setType(vehicle.getType());
        existingVehicle.setFuelType(vehicle.getFuelType());
        existingVehicle.setSeatingCapacity(vehicle.getSeatingCapacity());
        existingVehicle.setMileage(vehicle.getMileage());
        existingVehicle.setColor(vehicle.getColor());
        existingVehicle.setDescription(vehicle.getDescription());
        existingVehicle.setInsuranceExpiry(vehicle.getInsuranceExpiry());
        existingVehicle.setLastServiceDate(vehicle.getLastServiceDate());
        existingVehicle.setNextServiceDate(vehicle.getNextServiceDate());

        // Only update license plate if it's different and not already used
        if (!existingVehicle.getLicensePlate().equals(vehicle.getLicensePlate())) {
            if (vehicleRepository.existsByLicensePlate(vehicle.getLicensePlate())) {
                throw new RuntimeException("License plate already exists: " + vehicle.getLicensePlate());
            }
            existingVehicle.setLicensePlate(vehicle.getLicensePlate());
        }

        return vehicleRepository.save(existingVehicle);
    }

    public void deleteVehicle(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found: " + id));

        // Check if vehicle has any active bookings before deletion
        // This would require BookingService, so we'll set status to OUT_OF_SERVICE
        // instead
        vehicle.setStatus(VehicleStatus.OUT_OF_SERVICE);
        vehicleRepository.save(vehicle);
    }

    public void updateVehicleStatus(Long vehicleId, VehicleStatus status) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found: " + vehicleId));

        vehicle.setStatus(status);
        vehicleRepository.save(vehicle);
    }

    public void markVehicleInUse(Long vehicleId) {
        updateVehicleStatus(vehicleId, VehicleStatus.IN_USE);
    }

    public void markVehicleAvailable(Long vehicleId) {
        updateVehicleStatus(vehicleId, VehicleStatus.AVAILABLE);
    }

    public void markVehicleForMaintenance(Long vehicleId) {
        updateVehicleStatus(vehicleId, VehicleStatus.MAINTENANCE);
    }

    public List<Vehicle> findVehiclesNeedingService() {
        return vehicleRepository.findVehiclesNeedingService(LocalDateTime.now());
    }

    public List<Vehicle> findVehiclesWithExpiringInsurance(int daysAhead) {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(daysAhead);
        return vehicleRepository.findVehiclesWithExpiringInsurance(futureDate);
    }

    public void updateServiceDate(Long vehicleId, LocalDateTime serviceDate, LocalDateTime nextServiceDate) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found: " + vehicleId));

        vehicle.setLastServiceDate(serviceDate);
        vehicle.setNextServiceDate(nextServiceDate);
        vehicleRepository.save(vehicle);
    }

    public void updateInsuranceExpiry(Long vehicleId, LocalDateTime expiryDate) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found: " + vehicleId));

        vehicle.setInsuranceExpiry(expiryDate);
        vehicleRepository.save(vehicle);
    }

    public boolean isLicensePlateAvailable(String licensePlate) {
        return !vehicleRepository.existsByLicensePlate(licensePlate);
    }

    public boolean isLicensePlateAvailableForUpdate(String licensePlate, Long vehicleId) {
        Vehicle existingVehicle = vehicleRepository.findByLicensePlate(licensePlate);
        return existingVehicle == null || existingVehicle.getId().equals(vehicleId);
    }

    public long getTotalVehicleCount() {
        return vehicleRepository.count();
    }

    public long getAvailableVehicleCount() {
        return vehicleRepository.findByStatus(VehicleStatus.AVAILABLE).size();
    }

    public long getVehicleCountByStatus(VehicleStatus status) {
        return vehicleRepository.findByStatus(status).size();
    }
}