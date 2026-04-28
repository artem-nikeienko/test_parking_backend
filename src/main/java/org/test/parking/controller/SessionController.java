package org.test.parking.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.test.parking.controller.request.CheckInRequest;
import org.test.parking.controller.response.CheckOutResponse;
import org.test.parking.controller.response.ParkingSessionResponse;
import org.test.parking.domain.session.ParkingSession;
import org.test.parking.service.ParkingSessionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    private final ParkingSessionService service;

    public SessionController(ParkingSessionService service) {
        this.service = service;
    }

    @PostMapping("/lots/{lotId}")
    public ParkingSessionResponse checkIn(
            @PathVariable String lotId,
            @Valid @RequestBody CheckInRequest req
    ) {
        try {
            ParkingSession session = service.checkIn(lotId, req.getVehicle());
            return ParkingSessionResponse.fromSession(session);
        } catch (Exception e) {
            //TODO: handle exceptions properly, e.g. log them, emit events, etc.
            return new ParkingSessionResponse();
        }
    }

    @GetMapping("/lots/{lotId}")
    public List<ParkingSessionResponse> getActive() {
        return service.getActiveSessions().stream()
                .map(ParkingSessionResponse::fromSession)
                .toList();
    }

    @PostMapping("/{sessionId}/check-out")
    public CheckOutResponse checkOut(@PathVariable String id) {
        try {
            return service.checkOut(id);
        } catch (Exception e) {
            return new CheckOutResponse();
        }
    }
}
