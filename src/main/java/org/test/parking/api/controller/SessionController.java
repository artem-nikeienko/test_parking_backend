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
import org.test.parking.api.request.CheckInRequest;
import org.test.parking.api.response.CheckOutResponse;
import org.test.parking.api.response.ParkingSessionResponse;
import org.test.parking.exception.ConflictException;
import org.test.parking.exception.NotFoundException;
import org.test.parking.exception.domain.DomainException;
import org.test.parking.exception.domain.LotNotFoundException;
import org.test.parking.exception.domain.SessionAlreadyCompletedException;
import org.test.parking.exception.domain.SessionNotFoundException;
import org.test.parking.session.domain.ParkingSession;
import org.test.parking.session.service.ParkingSessionService;

import jakarta.validation.Valid;

//TODO: Add HATEOAS links
@RestController
@Validated
@RequestMapping("/api/v1/sessions")
public class SessionController {

    private final ParkingSessionService service;

    public SessionController(ParkingSessionService service) {
        this.service = service;
    }

    @PostMapping("/lots/{lotId}")
    public ResponseEntity<ParkingSessionResponse> checkIn(
            @PathVariable String lotId,
            @Valid @RequestBody CheckInRequest req
    ) throws NotFoundException {
        try {
            ParkingSession session = service.checkIn(lotId, req.getVehicle());
            URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{sessionId}")
                .buildAndExpand(session.getId())
                .toUri();
            return ResponseEntity.created(location).body(ParkingSessionResponse.success(session));
        } catch (LotNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (DomainException e) {
            //TODO: handle exceptions properly, e.g. log them, emit events, etc.
            return ResponseEntity.ok(ParkingSessionResponse.failed(e.getMessage()));
        }
    }

    @GetMapping("/lots/{lotId}")
    public List<ParkingSessionResponse> getActive(@PathVariable String lotId) {
        return service.getActiveSessions(lotId).stream()
                .map(ParkingSessionResponse::success)
                .toList();
    }

    @PostMapping("/{sessionId}/check-out")
    public ResponseEntity<CheckOutResponse> checkOut(@PathVariable String sessionId) throws NotFoundException, ConflictException {
        try {
            ParkingSession completedSession = service.checkOut(sessionId);
            return ResponseEntity.ok(CheckOutResponse.mapFromSession(completedSession));
        } catch (SessionNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (SessionAlreadyCompletedException e) {
            throw new ConflictException(e.getMessage());
        }
    }
}
