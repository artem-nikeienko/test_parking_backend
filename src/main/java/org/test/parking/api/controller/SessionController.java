package org.test.parking.api.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.test.parking.api.dto.CompletedSessionDto;
import org.test.parking.api.dto.NewParkingSessionDto;
import org.test.parking.api.dto.ParkingSessionDto;
import org.test.parking.api.request.CheckInRequest;
import org.test.parking.exception.NotFoundException;
import org.test.parking.exception.domain.DomainException;
import org.test.parking.exception.domain.SessionNotFoundException;
import org.test.parking.session.domain.ParkingSession;
import org.test.parking.session.service.ParkingSessionService;

import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/api/v1/sessions")
public class SessionController {

    private final ParkingSessionService service;

    public SessionController(ParkingSessionService service) {
        this.service = service;
    }

    @PostMapping("/lots/{lotId}")
    public ResponseEntity<NewParkingSessionDto> checkIn(
            @PathVariable String lotId,
            @Valid @RequestBody CheckInRequest req
    ) throws DomainException {
        ParkingSession session = service.checkIn(lotId, req.getVehicle());
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{sessionId}")
            .buildAndExpand(session.getId())
            .toUri();
        return ResponseEntity.created(location)
            .body(NewParkingSessionDto.from(session));
    }

    @GetMapping("/lots/{lotId}")
    public ResponseEntity<List<ParkingSessionDto>> getActive(@PathVariable String lotId) {
        var responseList = service.getActiveSessions(lotId).stream()
                .map(ParkingSessionDto::from)
                .toList();
        return ResponseEntity.ok(responseList);
    }

    @PostMapping("/{sessionId}/check-out")
    public ResponseEntity<CompletedSessionDto> checkOut(@PathVariable String sessionId) throws NotFoundException, DomainException {
        try {
            ParkingSession completedSession = service.checkOut(sessionId);
            return ResponseEntity.ok(CompletedSessionDto.mapFromSession(completedSession));
        } catch (SessionNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }
}
