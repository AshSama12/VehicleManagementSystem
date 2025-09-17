package com.company.vehiclemanagement.config;

import com.company.vehiclemanagement.model.*;
import com.company.vehiclemanagement.repository.UserRepository;
import com.company.vehiclemanagement.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create demo users if they don't exist
        createUserIfNotExists("admin", "admin123", "Admin", "User", "admin@company.com", "1001", "IT", Role.ADMIN);
        createUserIfNotExists("manager", "manager123", "Fleet", "Manager", "manager@company.com", "2001", "Operations",
                Role.FLEET_MANAGER);
        createUserIfNotExists("employee", "employee123", "John", "Doe", "john.doe@company.com", "3001", "Sales",
                Role.EMPLOYEE);
        createUserIfNotExists("jane.smith", "password123", "Jane", "Smith", "jane.smith@company.com", "3002",
                "Marketing", Role.EMPLOYEE);
        createUserIfNotExists("mike.wilson", "password123", "Mike", "Wilson", "mike.wilson@company.com", "3003",
                "Finance", Role.EMPLOYEE);

        // Create demo vehicles if they don't exist
        createVehicleIfNotExists("Toyota", "Camry", 2023, "ABC-123", VehicleType.SEDAN, 5);
        createVehicleIfNotExists("Honda", "CR-V", 2022, "DEF-456", VehicleType.SUV, 5);
        createVehicleIfNotExists("Ford", "Transit", 2023, "GHI-789", VehicleType.VAN, 8);
        createVehicleIfNotExists("BMW", "X5", 2023, "JKL-012", VehicleType.SUV, 7);
        createVehicleIfNotExists("Mercedes", "Sprinter", 2022, "MNO-345", VehicleType.VAN, 12);
        createVehicleIfNotExists("Audi", "A4", 2023, "PQR-678", VehicleType.SEDAN, 5);
        createVehicleIfNotExists("Volkswagen", "Tiguan", 2022, "STU-901", VehicleType.SUV, 5);
        createVehicleIfNotExists("Nissan", "Altima", 2023, "VWX-234", VehicleType.SEDAN, 5);
    }

    private void createUserIfNotExists(String username, String password, String firstName, String lastName,
            String email, String employeeId, String department, Role role) {
        if (!userRepository.findByUsername(username).isPresent()) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setEmployeeId(employeeId);
            user.setDepartment(department);
            user.setRole(role);
            user.setActive(true);
            user.setPhoneNumber("555-0" + employeeId.substring(1));
            userRepository.save(user);
        }
    }

    private void createVehicleIfNotExists(String make, String model, int year, String licensePlate,
            VehicleType type, int capacity) {
        if (vehicleRepository.findByLicensePlate(licensePlate) == null) {
            Vehicle vehicle = new Vehicle();
            vehicle.setMake(make);
            vehicle.setModel(model);
            vehicle.setYear(year);
            vehicle.setLicensePlate(licensePlate);
            vehicle.setType(type);
            vehicle.setSeatingCapacity(capacity);
            vehicle.setStatus(VehicleStatus.AVAILABLE);
            vehicle.setMileage(Math.random() * 50000); // Random mileage
            vehicle.setFuelType(FuelType.PETROL);
            vehicle.setInsuranceExpiry(LocalDateTime.now().plusMonths(6));
            vehicle.setLastServiceDate(LocalDateTime.now().minusMonths(2));
            vehicle.setNextServiceDate(LocalDateTime.now().plusMonths(4));
            vehicleRepository.save(vehicle);
        }
    }
}