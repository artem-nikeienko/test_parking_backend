package org.test.parking.lot.service;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.test.parking.exception.ConflictException;
import org.test.parking.exception.domain.LevelNotFoundException;
import org.test.parking.exception.domain.LotNotFoundException;
import org.test.parking.lot.domain.Level;
import org.test.parking.lot.domain.Lot;
import org.test.parking.lot.domain.Slot;
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
        String lotId = service.addLot("Lot A");

        // then
        assertNotNull(lotId);
        // assertEquals(name, lot.getName());

        assertTrue(lotRepository.findById(lotId).isPresent());
    }

    @Test
    void shouldAddLevelToLot() throws Exception {
        // given
        String lotId = service.addLot("Lot A");

        // when
        int levelNumber = service.addLevel(lotId, 1);

        // then
        Lot updated = lotRepository.findById(lotId).orElseThrow();
        assertEquals(1, updated.getLevels().size());
        assertEquals(1, levelNumber);
    }

    @Test
    void shouldAddSlotToLevel() throws Exception {
        // given
        String lotId = service.addLot("Lot A");
        service.addLevel(lotId, 1);

        // when
        int slotId = service.addSlot(lotId, 1, SlotType.COMPACT);

        // then
        Lot updated = lotRepository.findById(lotId).orElseThrow();
        Level level = updated.getLevels().stream().findFirst().get();

        assertEquals(1, level.getSlots().size());
    }

    @Test
    void shouldNotAllowDuplicateLevelNumber() throws Exception {
        // given
        String lotId = service.addLot("Lot A");
        service.addLevel(lotId, 1);

        assertThrows(ConflictException.class, () ->
            service.addLevel(lotId, 1)
        );
    }

    @Test
    void shouldFailWhenLevelNotExists() throws Exception {
        // given
        String lotId = service.addLot("Lot A");

        assertThrows(LevelNotFoundException.class, () ->
            service.addSlot(lotId, 999, SlotType.COMPACT)
        );
    }

    @Test
    void shouldUpdateSlotStatus() throws Exception {
        // given
        String lotId = service.addLot("Lot A");
        service.addLevel(lotId, 1);
        int slotId = service.addSlot(lotId, 1, SlotType.COMPACT);

        // when
        service.changeSlotStatus(lotId, 1, slotId, SlotStatus.UNAVAILABLE);

        // then
        Slot updated = lotRepository.findById(lotId)
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
        String lotId = service.addLot("Lot A");

        assertThrows(LevelNotFoundException.class, () ->
            service.removeLevel(lotId, 1)
        );
    }
}
