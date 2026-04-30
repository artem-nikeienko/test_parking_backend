package org.test.parking.api.response;

import java.time.LocalDateTime;

import org.test.parking.session.domain.ParkingSession;
import org.test.parking.session.domain.SessionStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSessionResponse {
    private String id;
    private SessionStatus status;
    private String licensePlate;
    private String slotId;
    private String levelId;
    private String lotId;
    private LocalDateTime entryTime;
    private String errorMessage;

    public static ParkingSessionResponse success(ParkingSession session) {
        return ParkingSessionResponse.builder()
            .id(session.getId())
            .status(session.getStatus())
            .licensePlate(session.getVehicle().getLicensePlate())
            .lotId(session.getSlotAssignment().getLotId())
            .levelId(session.getSlotAssignment().getLevelNumber().toString())
            .slotId(session.getSlotAssignment().getSlotId().toString())
            .entryTime(session.getEntryTime())
            .build();
    }

    public static ParkingSessionResponse failed(String errorMessage) {
        return ParkingSessionResponse.builder()
            .errorMessage(errorMessage)
            .build();
    }
}
