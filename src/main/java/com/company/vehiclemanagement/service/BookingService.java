package com.company.vehiclemanagement.service;

import com.company.vehiclemanagement.model.Booking;
import com.company.vehiclemanagement.model.BookingStatus;
import com.company.vehiclemanagement.model.User;
import com.company.vehiclemanagement.model.Vehicle;
import com.company.vehiclemanagement.model.VehicleStatus;
import com.company.vehiclemanagement.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private VehicleService vehicleService;

    public List<Booking> findAllBookings() {
        return bookingRepository.findAll();
    }

    public List<Booking> findBookingsByUser(User user) {
        return bookingRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public List<Booking> findBookingsByStatus(BookingStatus status) {
        return bookingRepository.findByStatusOrderByCreatedAtAsc(status);
    }

    public List<Booking> findPendingBookings() {
        return bookingRepository.findByStatusOrderByCreatedAtAsc(BookingStatus.PENDING);
    }

    public List<Booking> findBookingsByVehicle(Vehicle vehicle) {
        return bookingRepository.findByVehicle(vehicle);
    }

    public List<Booking> findBookingsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return bookingRepository.findBookingsBetweenDates(startDate, endDate);
    }

    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }

    public Booking createBooking(Booking booking) {
        // Validate booking dates
        validateBookingDates(booking.getStartDateTime(), booking.getEndDateTime());

        // Check for conflicts
        if (hasConflictingBookings(booking.getVehicle(), booking.getStartDateTime(), booking.getEndDateTime())) {
            throw new RuntimeException("Vehicle is already booked for the selected time period");
        }

        // Verify vehicle is available
        if (!booking.getVehicle().isAvailable()) {
            throw new RuntimeException("Vehicle is not available for booking");
        }

        // Set initial status
        booking.setStatus(BookingStatus.PENDING);

        return bookingRepository.save(booking);
    }

    public Booking updateBooking(Booking booking) {
        Booking existingBooking = bookingRepository.findById(booking.getId())
                .orElseThrow(() -> new RuntimeException("Booking not found: " + booking.getId()));

        // Only allow updates to pending bookings
        if (existingBooking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Can only modify pending bookings");
        }

        // Validate new dates
        validateBookingDates(booking.getStartDateTime(), booking.getEndDateTime());

        // Check for conflicts (excluding current booking)
        List<Booking> conflicts = bookingRepository.findConflictingBookings(
                booking.getVehicle(), booking.getStartDateTime(), booking.getEndDateTime());

        // Remove current booking from conflicts
        conflicts.removeIf(b -> b.getId().equals(booking.getId()));

        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Vehicle is already booked for the selected time period");
        }

        // Update booking details
        existingBooking.setVehicle(booking.getVehicle());
        existingBooking.setStartDateTime(booking.getStartDateTime());
        existingBooking.setEndDateTime(booking.getEndDateTime());
        existingBooking.setDestination(booking.getDestination());
        existingBooking.setPurpose(booking.getPurpose());

        return bookingRepository.save(existingBooking);
    }

    public void approveBooking(Long bookingId, User approver, String notes) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Can only approve pending bookings");
        }

        // Double-check for conflicts before approval
        if (hasConflictingBookings(booking.getVehicle(), booking.getStartDateTime(), booking.getEndDateTime())) {
            throw new RuntimeException("Vehicle has conflicting bookings and cannot be approved");
        }

        booking.approve(approver, notes);
        bookingRepository.save(booking);

        // Update vehicle status if booking starts soon (within 1 hour)
        if (booking.getStartDateTime().isBefore(LocalDateTime.now().plusHours(1))) {
            vehicleService.markVehicleInUse(booking.getVehicle().getId());
        }
    }

    public void rejectBooking(Long bookingId, User approver, String notes) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Can only reject pending bookings");
        }

        booking.reject(approver, notes);
        bookingRepository.save(booking);
    }

    public void cancelBooking(Long bookingId, User user) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));

        // Users can only cancel their own bookings
        if (!booking.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only cancel your own bookings");
        }

        // Can only cancel pending or approved bookings that haven't started
        if (booking.getStatus() == BookingStatus.COMPLETED || booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Cannot cancel completed or already cancelled bookings");
        }

        if (booking.getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cannot cancel bookings that have already started");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // Mark vehicle as available if it was in use
        if (booking.getVehicle().getStatus() == VehicleStatus.IN_USE) {
            vehicleService.markVehicleAvailable(booking.getVehicle().getId());
        }
    }

    public void completeBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));

        if (booking.getStatus() != BookingStatus.APPROVED) {
            throw new RuntimeException("Can only complete approved bookings");
        }

        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepository.save(booking);

        // Mark vehicle as available
        vehicleService.markVehicleAvailable(booking.getVehicle().getId());
    }

    public void processCompletedBookings() {
        // Find bookings that should be automatically completed
        List<Booking> completedBookings = bookingRepository.findCompletedBookings(LocalDateTime.now());

        for (Booking booking : completedBookings) {
            completeBooking(booking.getId());
        }
    }

    public boolean hasConflictingBookings(Vehicle vehicle, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<Booking> conflicts = bookingRepository.findConflictingBookings(vehicle, startDateTime, endDateTime);
        return !conflicts.isEmpty();
    }

    public List<Booking> findUserBookingsByStatus(User user, BookingStatus status) {
        return bookingRepository.findByUserAndStatus(user, status);
    }

    public long getTotalBookingCount() {
        return bookingRepository.count();
    }

    public long getBookingCountByStatus(BookingStatus status) {
        return bookingRepository.findByStatus(status).size();
    }

    public long getUserBookingCount(User user) {
        return bookingRepository.findByUser(user).size();
    }

    public long getVehicleBookingCount(Vehicle vehicle) {
        return bookingRepository.countApprovedBookingsByVehicle(vehicle);
    }

    private void validateBookingDates(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        LocalDateTime now = LocalDateTime.now();

        if (startDateTime.isBefore(now)) {
            throw new RuntimeException("Start date and time must be in the future");
        }

        if (endDateTime.isBefore(startDateTime)) {
            throw new RuntimeException("End date and time must be after start date and time");
        }

        // Minimum booking duration (1 hour)
        if (endDateTime.isBefore(startDateTime.plusHours(1))) {
            throw new RuntimeException("Minimum booking duration is 1 hour");
        }

        // Maximum booking duration (30 days)
        if (endDateTime.isAfter(startDateTime.plusDays(30))) {
            throw new RuntimeException("Maximum booking duration is 30 days");
        }

        // Cannot book more than 90 days in advance
        if (startDateTime.isAfter(now.plusDays(90))) {
            throw new RuntimeException("Cannot book more than 90 days in advance");
        }
    }
}