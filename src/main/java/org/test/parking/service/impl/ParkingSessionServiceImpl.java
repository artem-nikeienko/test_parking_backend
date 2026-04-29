package org.test.parking.service.impl;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.test.parking.controller.response.CheckOutResponse;
import org.test.parking.domain.model.session.ParkingSession;
import org.test.parking.domain.model.session.SessionStatus;
import org.test.parking.domain.model.session.SlotAssignment;
import org.test.parking.domain.model.space.Lot;
import org.test.parking.domain.model.vehicle.Vehicle;
import org.test.parking.exception.NoAvailableSlotsException;
import org.test.parking.exception.VehicleParkedException;
import org.test.parking.exception.domain.DomainException;
import org.test.parking.exception.domain.LotNotFoundException;
import org.test.parking.exception.domain.RestrictedSlotOperationException;
import org.test.parking.exception.domain.SessionNotFoundException;
import org.test.parking.repository.LotRepository;
import org.test.parking.repository.SessionRepository;
import org.test.parking.service.FeeService;
import org.test.parking.service.ParkingSessionService;
import org.test.parking.service.SlotAssignmentService;

@Service
public class ParkingSessionServiceImpl implements ParkingSessionService {

    private final LotRepository lotRepo;
    private final SessionRepository sessionRepo;
    private final FeeService feeService;
    private final SlotAssignmentService assignmentService;

    public ParkingSessionServiceImpl(
            LotRepository lotRepo,
            SessionRepository sessionRepo,
            FeeService feeService,
            SlotAssignmentService assignmentService
    ) {
        this.lotRepo = lotRepo;
        this.sessionRepo = sessionRepo;
        this.feeService = feeService;
        this.assignmentService = assignmentService;
    }

    @Override
    @Transactional
    public ParkingSession checkIn(String lotId, Vehicle vehicle)
      throws VehicleParkedException, LotNotFoundException, NoAvailableSlotsException, RestrictedSlotOperationException {
        Optional<ParkingSession> optSession = sessionRepo.findActiveByPlate(vehicle.getLicensePlate());
        if (optSession.isPresent()) {
            throw new VehicleParkedException("Vehicle with the same license plate is already parked");
        }

        Lot lot = getLotOrThrow(lotId);
        SlotAssignment slotAssignment = assignmentService.assignSlot(lot, vehicle);
        lotRepo.save(lot);

        ParkingSession session = ParkingSession.builder()
            .id(UUID.randomUUID().toString())
            .status(SessionStatus.ACTIVE)
            .vehicle(vehicle)
            .lotId(lotId)
            .slotAssignment(slotAssignment)
            //TODO: move entryTime to Controller layer and pass it as parameter to service
            .entryTime(LocalDateTime.now())
            .build();

        return sessionRepo.save(session);
    }

    @Override
    @Transactional
    public CheckOutResponse checkOut(String sessionId)
      throws SessionNotFoundException {
        ParkingSession session = sessionRepo.findById(sessionId)
            .orElseThrow(() -> new SessionNotFoundException("Session not found"));

        session.checkout();
        long minutes = Duration.between(session.getEntryTime(), session.getExitTime()).toMinutes();
        BigDecimal fee = feeService.calculate(session.getVehicle(), minutes);
        session.setFee(fee);
        sessionRepo.save(session);

        try {
            Lot sessionLot = getLotOrThrow(session.getLotId());
            sessionLot.releaseSlot(session.getSlotAssignment());
            lotRepo.save(sessionLot);
        } catch (DomainException e) {
            // This is a very rare case indicating potential data integrity issues that should be investigated,
            // but we still want to return checkout response with fee and timing details, so we log the error and continue without throwing exception further.
            // In real application, we would use a logger here to log the error with sessionId and lotId for further investigation.
            System.err.println(String.format("Error during slot release for session [%s]: %s", sessionId, e.getMessage()));
        } //TODO: Add Exception handling for all other exceptions

        return CheckOutResponse.builder()
                .licensePlate(session.getVehicle().getLicensePlate())
                .entryTime(session.getEntryTime())
                .exitTime(session.getExitTime())
                .durationMinutes(minutes)
                .fee(fee)
                .build();
    }

    @Override
    public List<ParkingSession> getActiveSessions(String lotId) {
        return sessionRepo.findAllActive(lotId);
    }

    private Lot getLotOrThrow(String lotId) throws LotNotFoundException {
        return lotRepo.findById(lotId).orElseThrow(() -> new LotNotFoundException("Lot not found"));
    }
}
