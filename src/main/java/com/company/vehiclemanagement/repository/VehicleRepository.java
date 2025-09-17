package com.company.vehiclemanagement.repository;

import com.company.vehiclemanagement.model.Vehicle;
import com.company.vehiclemanagement.model.VehicleStatus;
import com.company.vehiclemanagement.model.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByStatus(VehicleStatus status);

    List<Vehicle> findByType(VehicleType type);

    List<Vehicle> findBySeatingCapacityGreaterThanEqual(Integer capacity);

    List<Vehicle> findByMakeAndModel(String make, String model);

    Vehicle findByLicensePlate(String licensePlate);

    List<Vehicle> findByStatusOrderByMakeAscModelAsc(VehicleStatus status);

    @Query("SELECT v FROM Vehicle v WHERE v.status = 'AVAILABLE' AND v.id NOT IN " +
            "(SELECT b.vehicle.id FROM Booking b WHERE b.status = 'APPROVED' AND " +
            "((b.startDateTime <= :endDateTime) AND (b.endDateTime >= :startDateTime)))")
    List<Vehicle> findAvailableVehiclesBetween(@Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime);

    @Query("SELECT v FROM Vehicle v WHERE v.insuranceExpiry < :date")
    List<Vehicle> findVehiclesWithExpiringInsurance(@Param("date") LocalDateTime date);

    @Query("SELECT v FROM Vehicle v WHERE v.nextServiceDate < :date")
    List<Vehicle> findVehiclesNeedingService(@Param("date") LocalDateTime date);

    boolean existsByLicensePlate(String licensePlate);
}