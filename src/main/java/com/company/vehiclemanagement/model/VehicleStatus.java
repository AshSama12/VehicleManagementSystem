package com.company.vehiclemanagement.model;

public enum VehicleStatus {
    AVAILABLE("Available"),
    IN_USE("In Use"),
    MAINTENANCE("Under Maintenance"),
    OUT_OF_SERVICE("Out of Service"),
    RESERVED("Reserved");

    private final String displayName;

    VehicleStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}