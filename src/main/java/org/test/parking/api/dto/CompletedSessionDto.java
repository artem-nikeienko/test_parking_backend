package org.test.parking.api.dto;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

import org.test.parking.session.domain.ParkingSession;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CompletedSessionDto {
    private String licensePlate;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private long durationMinutes;
    private BigDecimal fee;

    public static CompletedSessionDto mapFromSession(ParkingSession session) {
        return CompletedSessionDto.builder()
            .licensePlate(session.getVehicle().getLicensePlate())
            .entryTime(session.getEntryTime())
            .exitTime(session.getExitTime())
            .durationMinutes(Duration.between(session.getEntryTime(), session.getExitTime()).toMinutes())
            .fee(session.getFee())
            .build();
    }
}