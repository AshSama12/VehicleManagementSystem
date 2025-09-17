package com.company.vehiclemanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User is required")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    @NotNull(message = "Vehicle is required")
    private Vehicle vehicle;

    @Column(name = "start_date_time", nullable = false)
    @NotNull(message = "Start date and time is required")
    @Future(message = "Start date and time must be in the future")
    private LocalDateTime startDateTime;

    @Column(name = "end_date_time", nullable = false)
    @NotNull(message = "End date and time is required")
    @Future(message = "End date and time must be in the future")
    private LocalDateTime endDateTime;

    @NotBlank(message = "Destination is required")
    private String destination;

    @Column(name = "purpose", length = 500)
    private String purpose;

    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.PENDING;

    @Column(name = "approval_notes", length = 500)
    private String approvalNotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Booking() {
    }

    public Booking(User user, Vehicle vehicle, LocalDateTime startDateTime, LocalDateTime endDateTime,
            String destination) {
        this.user = user;
        this.vehicle = vehicle;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.destination = destination;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public String getApprovalNotes() {
        return approvalNotes;
    }

    public void setApprovalNotes(String approvalNotes) {
        this.approvalNotes = approvalNotes;
    }

    public User getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(User approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
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

    public boolean isPending() {
        return status == BookingStatus.PENDING;
    }

    public boolean isApproved() {
        return status == BookingStatus.APPROVED;
    }

    public boolean isRejected() {
        return status == BookingStatus.REJECTED;
    }

    public boolean isCompleted() {
        return status == BookingStatus.COMPLETED;
    }

    public void approve(User approver, String notes) {
        this.status = BookingStatus.APPROVED;
        this.approvedBy = approver;
        this.approvedAt = LocalDateTime.now();
        this.approvalNotes = notes;
    }

    public void reject(User approver, String notes) {
        this.status = BookingStatus.REJECTED;
        this.approvedBy = approver;
        this.approvedAt = LocalDateTime.now();
        this.approvalNotes = notes;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", user=" + (user != null ? user.getUsername() : "null") +
                ", vehicle=" + (vehicle != null ? vehicle.getDisplayName() : "null") +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                ", destination='" + destination + '\'' +
                ", status=" + status +
                '}';
    }
}