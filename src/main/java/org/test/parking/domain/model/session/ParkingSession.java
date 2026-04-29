package org.test.parking.domain.model.session;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

import org.test.parking.domain.model.vehicle.Vehicle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class ParkingSession {

    private final String id;
    private final Vehicle vehicle;
    private final String lotId;
    private final SlotAssignment slotAssignment;

    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;

    private SessionStatus status;

    private BigDecimal fee;

    public void checkout() {
        //ASSUMPTION: we assume that exitTime is set at the service layer, calling this method, but isn't it better to pass it outside, from Controller level?
        exitTime = LocalDateTime.now();
        status = SessionStatus.COMPLETED;
    }

    public Duration getDuration() {
        return Duration.between(entryTime, exitTime);
    }

    public boolean isActive() {
        return status == SessionStatus.ACTIVE;
    }

}
