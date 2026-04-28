package org.test.parking.service.impl;

import org.test.parking.slot.SlotAllocationStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.test.parking.controller.response.CheckOutResponse;
import org.test.parking.domain.session.ParkingSession;
import org.test.parking.domain.session.SessionStatus;
import org.test.parking.domain.session.SlotStatus;
import org.test.parking.domain.space.Lot;
import org.test.parking.domain.space.Slot;
import org.test.parking.domain.space.SlotType;
import org.test.parking.domain.vehicle.Vehicle;
import org.test.parking.domain.vehicle.VehicleType;
import org.test.parking.exception.ConflictException;
import org.test.parking.exception.NotFoundException;
import org.test.parking.repository.LotRepository;
import org.test.parking.repository.SessionRepository;
import org.test.parking.service.FeeService;
import org.test.parking.service.ParkingSessionService;

@Service
public class ParkingServiceImpl implements ParkingSessionService {

    private final LotRepository lotRepo;
    private final SessionRepository sessionRepo;
    private final FeeService feeService;
    private final SlotAllocationStrategy allocationStrategy;

    public ParkingServiceImpl(
            LotRepository lotRepo,
            SessionRepository sessionRepo,
            FeeService feeService,
            SlotAllocationStrategy allocationStrategy
    ) {
        this.lotRepo = lotRepo;
        this.sessionRepo = sessionRepo;
        this.feeService = feeService;
        this.allocationStrategy = allocationStrategy;
    }

    @Override
    @Transactional
    public ParkingSession checkIn(String lotId, Vehicle vehicle) throws ConflictException, NotFoundException {
        Optional<ParkingSession> optSession = sessionRepo.findActiveByPlate(vehicle.getLicensePlate());
        if (optSession.isPresent()) {
            throw new ConflictException("Vehicle with the same license plate is already parked");
        }

        Lot lot = getLotOrThrow(lotId);
        
        Optional<Slot> optAllocatedSlot = lot.allocateSlot(vehicle, allocationStrategy);
        
        if (optAllocatedSlot.isEmpty()) {
            throw new ConflictException("No available slot");
        }
        lotRepo.save(lot);

        ParkingSession session = ParkingSession.builder()
                .id(UUID.randomUUID().toString())
                .status(SessionStatus.ACTIVE)
                .vehicle(vehicle)
                .lot(lot)
                .slot(optAllocatedSlot.get())
                //TODO: move entryTime to Controller layer and pass it as parameter to service
                .entryTime(LocalDateTime.now())
                .build();

        return sessionRepo.save(session);
    }

    @Override
    @Transactional
    public CheckOutResponse checkOut(String sessionId) throws NotFoundException {
        ParkingSession session = sessionRepo.findById(sessionId)
            .orElseThrow(() -> new NotFoundException("Session not found"));

        session.checkout();
        long minutes = Duration.between(session.getEntryTime(), session.getExitTime()).toMinutes();
        BigDecimal fee = feeService.calculate(session.getVehicle(), minutes);
        session.setFee(fee);
        sessionRepo.save(session);

        Lot lot = session.getLot();
        lot.releaseSlot(session.getLevelNumber(), session.getSlotId());
        lotRepo.save(lot);


        return CheckOutResponse.builder()
                .licensePlate(session.getVehicle().getLicensePlate())
                .entryTime(session.getEntryTime())
                .exitTime(session.getExitTime())
                .durationMinutes(minutes)
                .fee(fee)
                .build();
    }

    @Override
    public List<ParkingSession> getActiveSessions() {
        return sessionRepo.findAllActive();
    }

    private Lot getLotOrThrow(String lotId) throws NotFoundException {
        return lotRepo.findById(lotId).orElseThrow(() -> new NotFoundException("Lot not found"));
    }
}
