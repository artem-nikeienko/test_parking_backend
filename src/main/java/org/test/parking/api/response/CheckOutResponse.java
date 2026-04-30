package org.test.parking.api.response;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

import org.test.parking.session.domain.ParkingSession;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckOutResponse {
    private String licensePlate;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private long durationMinutes;
    private BigDecimal fee;

    public static CheckOutResponse mapFromSession(ParkingSession session) {
        return CheckOutResponse.builder()
            .licensePlate(session.getVehicle().getLicensePlate())
            .entryTime(session.getEntryTime())
            .exitTime(session.getExitTime())
            .durationMinutes(Duration.between(session.getEntryTime(), session.getExitTime()).toMinutes())
            .fee(session.getFee())
            .build();
    }
}