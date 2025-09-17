package com.company.vehiclemanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Vehicle make is required")
    private String make;

    @NotBlank(message = "Vehicle model is required")
    private String model;

    @Column(name = "model_year")
    private Integer year;

    @NotBlank(message = "License plate is required")
    @Column(name = "license_plate", unique = true)
    private String licensePlate;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Vehicle type is required")
    private VehicleType type;

    @Enumerated(EnumType.STRING)
    private FuelType fuelType;

    @Positive(message = "Seating capacity must be positive")
    @Column(name = "seating_capacity")
    private Integer seatingCapacity;

    @Column(name = "mileage")
    private Double mileage;

    @Column(name = "color")
    private String color;

    @Enumerated(EnumType.STRING)
    private VehicleStatus status = VehicleStatus.AVAILABLE;

    @Column(name = "insurance_expiry")
    private LocalDateTime insuranceExpiry;

    @Column(name = "last_service_date")
    private LocalDateTime lastServiceDate;

    @Column(name = "next_service_date")
    private LocalDateTime nextServiceDate;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Booking> bookings = new HashSet<>();

    // Constructors
    public Vehicle() {
    }

    public Vehicle(String make, String model, String licensePlate, VehicleType type, Integer seatingCapacity) {
        this.make = make;
        this.model = model;
        this.licensePlate = licensePlate;
        this.type = type;
        this.seatingCapacity = seatingCapacity;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }

    public Integer getSeatingCapacity() {
        return seatingCapacity;
    }

    public void setSeatingCapacity(Integer seatingCapacity) {
        this.seatingCapacity = seatingCapacity;
    }

    public Double getMileage() {
        return mileage;
    }

    public void setMileage(Double mileage) {
        this.mileage = mileage;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    public LocalDateTime getInsuranceExpiry() {
        return insuranceExpiry;
    }

    public void setInsuranceExpiry(LocalDateTime insuranceExpiry) {
        this.insuranceExpiry = insuranceExpiry;
    }

    public LocalDateTime getLastServiceDate() {
        return lastServiceDate;
    }

    public void setLastServiceDate(LocalDateTime lastServiceDate) {
        this.lastServiceDate = lastServiceDate;
    }

    public LocalDateTime getNextServiceDate() {
        return nextServiceDate;
    }

    public void setNextServiceDate(LocalDateTime nextServiceDate) {
        this.nextServiceDate = nextServiceDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(Set<Booking> bookings) {
        this.bookings = bookings;
    }

    public String getDisplayName() {
        return year != null ? year + " " + make + " " + model : make + " " + model;
    }

    public boolean isAvailable() {
        return status == VehicleStatus.AVAILABLE;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", licensePlate='" + licensePlate + '\'' +
                ", type=" + type +
                ", status=" + status +
                '}';
    }
}