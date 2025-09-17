package com.company.vehiclemanagement.model;

public enum Role {
    EMPLOYEE("Employee"),
    ADMIN("Administrator"),
    FLEET_MANAGER("Fleet Manager");

    private final String displayName;

    Role(String displayName) {
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