package org.test.parking.controller.response;

import java.time.LocalDateTime;

import org.test.parking.domain.model.session.ParkingSession;
import org.test.parking.domain.model.session.SessionStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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

    public static ParkingSessionResponse fromSession(ParkingSession session) {
        return ParkingSessionResponse.builder()
                // .id(session.getId())
                // .status(session.getStatus())
                .licensePlate(session.getVehicle().getLicensePlate())
                // .slotId(session.getSlot().getId())
                // .levelId(session.getLevelNumber())
                .lotId(session.getLotId())
                .entryTime(session.getEntryTime())
                .build();
    }
}
