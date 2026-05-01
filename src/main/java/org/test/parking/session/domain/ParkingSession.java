package org.test.parking.session.domain;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import org.test.parking.vehicle.domain.Vehicle;

import lombok.Getter;

@Getter
public class ParkingSession {

    private final String id;
    private final Vehicle vehicle;
    private final SlotAssignment slotAssignment;

    private LocalDateTime entryTime;
    private LocalDateTime exitTime;

    private SessionStatus status;

    private BigDecimal fee;

    public ParkingSession(Vehicle vehicle, SlotAssignment slotAssignment) {
        this.id = UUID.randomUUID().toString();
        this.vehicle = vehicle;
        this.slotAssignment = slotAssignment;
    }

    public void checkOut() {
        exitTime = LocalDateTime.now();
        status = SessionStatus.COMPLETED;
    }

    public void checkIn() {
        entryTime = LocalDateTime.now();
        status = SessionStatus.ACTIVE;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public Duration getDuration() {
        return Duration.between(entryTime, exitTime);
    }

    public boolean isActive() {
        return status == SessionStatus.ACTIVE;
    }

}
