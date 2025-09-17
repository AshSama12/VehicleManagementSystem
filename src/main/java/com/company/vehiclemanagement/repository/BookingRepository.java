package com.company.vehiclemanagement.repository;

import com.company.vehiclemanagement.model.Booking;
import com.company.vehiclemanagement.model.BookingStatus;
import com.company.vehiclemanagement.model.User;
import com.company.vehiclemanagement.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUser(User user);

    List<Booking> findByVehicle(Vehicle vehicle);

    List<Booking> findByStatus(BookingStatus status);

    List<Booking> findByUserOrderByCreatedAtDesc(User user);

    List<Booking> findByStatusOrderByCreatedAtAsc(BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.vehicle = :vehicle AND b.status = 'APPROVED' AND " +
            "((b.startDateTime <= :endDateTime) AND (b.endDateTime >= :startDateTime))")
    List<Booking> findConflictingBookings(@Param("vehicle") Vehicle vehicle,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime);

    @Query("SELECT b FROM Booking b WHERE b.startDateTime BETWEEN :startDate AND :endDate")
    List<Booking> findBookingsBetweenDates(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT b FROM Booking b WHERE b.user = :user AND b.status = :status")
    List<Booking> findByUserAndStatus(@Param("user") User user, @Param("status") BookingStatus status);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.vehicle = :vehicle AND b.status = 'APPROVED'")
    Long countApprovedBookingsByVehicle(@Param("vehicle") Vehicle vehicle);

    @Query("SELECT b FROM Booking b WHERE b.endDateTime < :now AND b.status = 'APPROVED'")
    List<Booking> findCompletedBookings(@Param("now") LocalDateTime now);
}