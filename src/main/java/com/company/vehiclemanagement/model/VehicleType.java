package com.company.vehiclemanagement.model;

public enum VehicleType {
    SEDAN("Sedan"),
    SUV("SUV"),
    HATCHBACK("Hatchback"),
    TRUCK("Truck"),
    VAN("Van"),
    COUPE("Coupe"),
    CONVERTIBLE("Convertible"),
    MINIBUS("Mini Bus"),
    BUS("Bus");

    private final String displayName;

    VehicleType(String displayName) {
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