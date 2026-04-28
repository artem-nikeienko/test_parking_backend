package org.test.parking.domain.session;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

import org.test.parking.domain.space.Lot;
import org.test.parking.domain.space.Slot;
import org.test.parking.domain.vehicle.Vehicle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class ParkingSession {

    private final String id;
    private final Vehicle vehicle;
    private final Slot slot;
    private final Lot lot;

    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;

    private SessionStatus status;

    private BigDecimal fee;

    public void checkout() {
        //ASSUMPTION: we assume that exitTime is set at the service layer, calling this method, but isn't it better to pass it outside, from Controller level?
        this.exitTime = LocalDateTime.now();
        this.status = SessionStatus.COMPLETED;
    }

    public Duration getDuration() {
        return Duration.between(entryTime, exitTime);
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public String getId() {
        return id;
    }

    public Lot getLot() {
        return lot;
    }

    public Slot getSlot() {
        return slot;
    }

    public int getLevelNumber() {
        return slot.getLevelNumber();
    }

    public int getSlotId() {
        return slot.getId();
    }

    public SessionStatus getStatus() {
        return status;
    }

    public boolean isActive() {
        return status == SessionStatus.ACTIVE;
    }

}
