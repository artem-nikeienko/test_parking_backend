package org.test.parking.lot.service;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.test.parking.exception.domain.ConflictException;
import org.test.parking.exception.domain.LevelNotFoundException;
import org.test.parking.exception.domain.LotNotFoundException;
import org.test.parking.lot.domain.LevelDto;
import org.test.parking.lot.domain.Lot;
import org.test.parking.lot.domain.LotDto;
import org.test.parking.lot.domain.SlotDto;
import org.test.parking.lot.domain.SlotType;
import org.test.parking.lot.repository.LotRepository;
import org.test.parking.lot.repository.impl.InMemoryLotRepository;
import org.test.parking.lot.service.impl.ParkingLotServiceImpl;
import org.test.parking.session.domain.SlotStatus;

public class ParkingLotServiceTest {

    private ParkingLotService service;
    private LotRepository lotRepository;

    @BeforeEach
    void setUp() {
        lotRepository = new InMemoryLotRepository();
        service = new ParkingLotServiceImpl(lotRepository);
    }

    @Test
    void shouldCreateLot() throws Exception {
        // given
        // when
        LotDto lot = service.addLot("Lot A");

        // then
        assertNotNull(lot.getId());
        // assertEquals(name, lot.getName());

        assertTrue(lotRepository.findById(lot.getId()).isPresent());
    }

    @Test
    void shouldAddLevelToLot() throws Exception {
        // given
        LotDto lot = service.addLot("Lot A");

        // when
        LevelDto addedLevel = service.addLevel(lot.getId(), 1);

        // then
        Lot updated = lotRepository.findById(lot.getId()).orElseThrow();
        assertEquals(1, updated.getLevels().size());
        assertEquals(1, addedLevel.getNumber());
    }

    @Test
    void shouldAddSlotToLevel() throws Exception {
        // given
        LotDto lot = service.addLot("Lot A");
        service.addLevel(lot.getId(), 1);

        // when
        SlotDto slot = service.addSlot(lot.getId(), 1, SlotType.COMPACT);

        // then
        Lot updated = lotRepository.findById(lot.getId()).orElseThrow();
        LevelDto level = updated.getLevels().get(0);

        assertEquals(1, level.getSlots().size());
    }

    @Test
    void shouldNotAllowDuplicateLevelNumber() throws Exception {
        // given
        LotDto lot = service.addLot("Lot A");
        service.addLevel(lot.getId(), 1);

        assertThrows(ConflictException.class, () ->
            service.addLevel(lot.getId(), 1)
        );
    }

    @Test
    void shouldFailWhenLevelNotExists() throws Exception {
        // given
        LotDto lot = service.addLot("Lot A");

        assertThrows(LevelNotFoundException.class, () ->
            service.addSlot(lot.getId(), 999, SlotType.COMPACT)
        );
    }

    @Test
    void shouldUpdateSlotStatus() throws Exception {
        // given
        LotDto lot = service.addLot("Lot A");
        service.addLevel(lot.getId(), 1);
        SlotDto slot = service.addSlot(lot.getId(), 1, SlotType.COMPACT);

        // when
        service.changeSlotStatus(lot.getId(), 1, slot.getId(), SlotStatus.UNAVAILABLE);

        // then
        SlotDto updated = lotRepository.findById(lot.getId())
            .orElseThrow()
            .getLevels().iterator().next()
            .getSlots().iterator().next();

        assertEquals(SlotStatus.UNAVAILABLE, updated.getStatus());
    }

    @Test
    void shouldThrowWhenLotNotFound() {
        assertThrows(LotNotFoundException.class, () ->
            service.addLevel(UUID.randomUUID().toString(), 1)
        );
    }

    @Test
    void shouldThrowWhenDeletingNonExistingLevel() throws Exception {
        // given
        LotDto lot = service.addLot("Lot A");

        assertThrows(LevelNotFoundException.class, () ->
            service.removeLevel(lot.getId(), 1)
        );
    }
}
