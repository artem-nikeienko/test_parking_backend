package org.test.parking.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.test.parking.session.domain.ParkingSession;
import org.test.parking.session.domain.SessionStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ParkingSessionDto {
    private String id;
    private SessionStatus status;
    private String licensePlate;
    private String lotId;
    private int levelNumber;
    private int slotId;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private BigDecimal fee;

    public static ParkingSessionDto from(ParkingSession session) {
        return ParkingSessionDto.builder()
            .id(session.getId())
            .status(session.getStatus())
            .licensePlate(session.getVehicle().getLicensePlate())
            .lotId(session.getSlotAssignment().getLotId())
            .levelNumber(session.getSlotAssignment().getLevelNumber())
            .slotId(session.getSlotAssignment().getSlotId())
            .entryTime(session.getEntryTime())
            .exitTime(session.getExitTime())
            .build();
    }
}
