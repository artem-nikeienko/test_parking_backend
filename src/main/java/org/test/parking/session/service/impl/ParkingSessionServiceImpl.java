package org.test.parking.session.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.test.parking.assignment.service.SlotAssignmentService;
import org.test.parking.exception.domain.DomainException;
import org.test.parking.exception.domain.LotNotFoundException;
import org.test.parking.exception.domain.NoAvailableSlotsException;
import org.test.parking.exception.domain.RestrictedLotOperationException;
import org.test.parking.exception.domain.SessionAlreadyCompletedException;
import org.test.parking.exception.domain.SessionNotFoundException;
import org.test.parking.exception.domain.VehicleParkedException;
import org.test.parking.lot.domain.Lot;
import org.test.parking.lot.repository.LotRepository;
import org.test.parking.pricing.service.FeeService;
import org.test.parking.session.domain.ParkingSession;
import org.test.parking.session.domain.SessionStatus;
import org.test.parking.session.domain.SlotAssignment;
import org.test.parking.session.repository.SessionRepository;
import org.test.parking.session.service.ParkingSessionService;
import org.test.parking.vehicle.domain.Vehicle;

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
      throws VehicleParkedException, LotNotFoundException, NoAvailableSlotsException, RestrictedLotOperationException {

        throwIfParkedAlready(vehicle);

        SlotAssignment slotAssignment = tryToAssignSlot(lotId, vehicle);

        ParkingSession session = tryToCheckInNewSession(vehicle, slotAssignment);

        return session;
    }

    @Override
    @Transactional
    public ParkingSession checkOut(String sessionId)
      throws SessionNotFoundException, SessionAlreadyCompletedException {
        ParkingSession session = sessionRepo.findById(sessionId)
            .orElseThrow(() -> new SessionNotFoundException("Session not found"));
        
        throwIfCompleted(session);

        ParkingSession checkedOutSession = tryToCheckOutAndSetFee(session);

        tryToReleaseParkingSlot(checkedOutSession);
        
        return checkedOutSession;
    }

    @Override
    public List<ParkingSession> getActiveSessions(String lotId) {
        return sessionRepo.findAllActive(lotId);
    }

    private Lot getLotOrThrow(String lotId) throws LotNotFoundException {
        return lotRepo.findById(lotId).orElseThrow(() -> new LotNotFoundException(lotId));
    }

    private void throwIfParkedAlready(Vehicle vehicle) throws VehicleParkedException {
        Optional<ParkingSession> optSession = sessionRepo.findActiveByPlate(vehicle.getLicensePlate());
        if (optSession.isPresent()) {
            throw new VehicleParkedException("Vehicle with the same license plate is already parked");
        }
    }
    
    private SlotAssignment tryToAssignSlot(String lotId, Vehicle vehicle) throws LotNotFoundException, NoAvailableSlotsException {
        Lot lot = getLotOrThrow(lotId);
        SlotAssignment slotAssignment = assignmentService.assignSlot(lot, vehicle);
        lotRepo.save(lot);
        return slotAssignment;
    }

    private ParkingSession tryToCheckInNewSession(Vehicle vehicle, SlotAssignment slotAssignment) {
        ParkingSession session = new ParkingSession(vehicle, slotAssignment);
        session.checkIn();
        return sessionRepo.save(session);
    }

    private void throwIfCompleted(ParkingSession session) throws SessionAlreadyCompletedException {
        if (session.getStatus() == SessionStatus.COMPLETED) {
            throw new SessionAlreadyCompletedException(session.getId());
        }
    }

    private ParkingSession tryToCheckOutAndSetFee(ParkingSession session) {
        session.checkOut();
        BigDecimal fee = feeService.calculate(session);
        session.setFee(fee);
        return sessionRepo.save(session);
    }
    
    private void tryToReleaseParkingSlot(ParkingSession session) {
        try {
            Lot sessionLot = getLotOrThrow(session.getSlotAssignment().getLotId());
            sessionLot.releaseSlot(session.getSlotAssignment());
            lotRepo.save(sessionLot);
        } catch (DomainException e) {
            System.err.println(String.format("Error during slot release for session [%s]: %s", session.getId(), e.getMessage()));
        }
    }
}
