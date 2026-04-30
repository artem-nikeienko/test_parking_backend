package org.test.parking.session;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.test.parking.assignment.domain.SlotAssigmentStrategy;
import org.test.parking.assignment.domain.impl.FirstFitSlotAssignmentStrategy;
import org.test.parking.assignment.query.SlotQueryService;
import org.test.parking.assignment.query.impl.FindAvailableAndCompatibleSlotQueryService;
import org.test.parking.assignment.service.SlotAssignmentService;
import org.test.parking.assignment.service.impl.SlotAssignmentServiceImpl;
import org.test.parking.exception.domain.LotNotFoundException;
import org.test.parking.exception.domain.NoAvailableSlotsException;
import org.test.parking.exception.domain.SessionAlreadyCompletedException;
import org.test.parking.exception.domain.SessionNotFoundException;
import org.test.parking.exception.domain.VehicleParkedException;
import org.test.parking.lot.domain.Lot;
import org.test.parking.lot.domain.Slot;
import org.test.parking.lot.domain.SlotType;
import org.test.parking.lot.domain.policy.SlotAvailabilityPolicy;
import org.test.parking.lot.domain.policy.SlotCompatibilityPolicy;
import org.test.parking.lot.domain.policy.impl.JustFitSlotCompatibilityPolicy;
import org.test.parking.lot.domain.policy.impl.OnlyVehicleForSlotAvailabilityPolicy;
import org.test.parking.lot.repository.LotRepository;
import org.test.parking.lot.repository.impl.InMemoryLotRepository;
import org.test.parking.pricing.domain.FeeStrategyFactory;
import org.test.parking.pricing.service.FeeService;
import org.test.parking.pricing.service.impl.FeeServiceImpl;
import org.test.parking.session.domain.ParkingSession;
import org.test.parking.session.domain.SessionStatus;
import org.test.parking.session.domain.SlotStatus;
import org.test.parking.session.repository.SessionRepository;
import org.test.parking.session.repository.impl.InMemorySessionRepository;
import org.test.parking.session.service.ParkingSessionService;
import org.test.parking.session.service.impl.ParkingSessionServiceImpl;
import org.test.parking.vehicle.domain.Vehicle;
import org.test.parking.vehicle.domain.VehicleType;

public class ParkingSessionServiceTest {

    private ParkingSessionService service;

    SlotAssigmentStrategy assigmentStrategy;
    SlotCompatibilityPolicy compatibilityPolicy;
    SlotAvailabilityPolicy availabilityPolicy;

    private SessionRepository sessionRepository;
    private LotRepository lotRepository;

    private SlotAssignmentService slotAssignmentService;
    private FeeService feeService;

    private FeeStrategyFactory feeStrategyFactory;
    private SlotQueryService slotQueryService;

    @BeforeEach
    void setUp() {

        assigmentStrategy = new FirstFitSlotAssignmentStrategy();
        compatibilityPolicy = new JustFitSlotCompatibilityPolicy();
        availabilityPolicy = new OnlyVehicleForSlotAvailabilityPolicy(); 
        sessionRepository = new InMemorySessionRepository();
        slotQueryService = new FindAvailableAndCompatibleSlotQueryService(assigmentStrategy, compatibilityPolicy, availabilityPolicy);
        lotRepository = new InMemoryLotRepository();
        slotAssignmentService = new SlotAssignmentServiceImpl(slotQueryService);
        feeStrategyFactory = new FeeStrategyFactory();
        feeService = new FeeServiceImpl(feeStrategyFactory);

        service = new ParkingSessionServiceImpl(
            lotRepository,
            sessionRepository,
            feeService,
            slotAssignmentService
        );
    }

    @Test
    void shouldCheckInVehicle() throws Exception {
        Lot lot = createLotWithSingleSlot();

        Vehicle vehicle = new Vehicle("AA1234", VehicleType.CAR);

        // when
        ParkingSession session = service.checkIn(lot.getId(), vehicle);

        // then
        assertEquals(SessionStatus.ACTIVE, session.getStatus());
        assertNotNull(session.getSlotAssignment());

        Lot updated = lotRepository.findById(lot.getId()).orElseThrow();
        Slot slot = updated.getLevels().iterator().next().getSlots().iterator().next();

        assertEquals(SlotStatus.OCCUPIED, slot.getStatus());
    }

    @Test
    void shouldCheckOutVehicle() throws Exception {
        // given
        Lot lot = createLotWithSingleSlot();

        Vehicle vehicle = new Vehicle("AA1234", VehicleType.CAR);
        ParkingSession checkInSession = service.checkIn(lot.getId(), vehicle);

        // when
        ParkingSession checkOutSession = service.checkOut(checkInSession.getId());

        // then
        assertEquals(vehicle.getLicensePlate(), checkOutSession.getVehicle().getLicensePlate());
        assertTrue(checkOutSession.getFee().compareTo(BigDecimal.ZERO) >= 0);

        ParkingSession stored = sessionRepository.findById(checkInSession.getId()).orElseThrow();
        assertEquals(SessionStatus.COMPLETED, stored.getStatus());
    }

    @Test
    void shouldNotAllowDuplicateActiveSession() throws Exception {
        Lot lot = createLotWithSingleSlot();

        Vehicle vehicle = new Vehicle("AA1234", VehicleType.CAR);

        service.checkIn(lot.getId(), vehicle);

        assertThrows(VehicleParkedException.class, () ->
            service.checkIn(lot.getId(), vehicle)
        );
    }

    @Test
    void shouldFailWhenNoSlotsAvailable() throws Exception {
        Lot lot = createLotWithSingleSlot();

        Vehicle v1 = new Vehicle("A1", VehicleType.CAR);
        Vehicle v2 = new Vehicle("A2", VehicleType.CAR);

        service.checkIn(lot.getId(), v1);

        assertThrows(NoAvailableSlotsException.class, () ->
            service.checkIn(lot.getId(), v2)
        );
    }

    @Test
    void shouldNotAllowDoubleCheckout() throws Exception {
        Lot lot = createLotWithSingleSlot();

        Vehicle vehicle = new Vehicle("AA1234", VehicleType.CAR);
        ParkingSession session = service.checkIn(lot.getId(), vehicle);

        service.checkOut(session.getId());

        assertThrows(SessionAlreadyCompletedException.class, () ->
            service.checkOut(session.getId())
        );
    }

    @Test
    void shouldThrowWhenSessionNotFound() {
        assertThrows(SessionNotFoundException.class, () ->
            service.checkOut(UUID.randomUUID().toString())
        );
    }

    @Test
    void shouldIgnoreLotRemovedByMistakeDuringSession() throws Exception {
        Lot lot = createLotWithSingleSlot();

        Vehicle vehicle = new Vehicle("AA1234", VehicleType.CAR);
        ParkingSession session = service.checkIn(lot.getId(), vehicle);

        lotRepository.delete(lot.getId());

        assertDoesNotThrow(() ->
            service.checkOut(session.getId())
        );
    }

    private Lot createLotWithSingleSlot() throws Exception {
        Lot lot = lotRepository.save(new Lot("Lot A"));
        int levelNumber = lot.addLevel(1);
        lot.addSlot(levelNumber, SlotType.COMPACT);
        return lot;
    }
}
