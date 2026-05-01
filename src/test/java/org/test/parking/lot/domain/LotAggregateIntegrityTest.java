package org.test.parking.lot.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.test.parking.exception.domain.ConflictException;
import org.test.parking.exception.domain.LevelNotFoundException;
import org.test.parking.exception.domain.RestrictedLotOperationException;
import org.test.parking.exception.domain.SlotNotFoundException;
import org.test.parking.session.domain.SlotAssignment;
import org.test.parking.session.domain.SlotStatus;

public class LotAggregateIntegrityTest {

    private Lot lot;

    @BeforeEach
    void setUp() {
        lot = new Lot("Test Lot");
    }

    @Test()
    void shouldAddLevelSuccessfully() {
        LevelDto addedLevel = assertDoesNotThrow(() -> 
            lot.addLevel(1));

        assertEquals(1, addedLevel.getNumber());
        assertEquals(1, lot.getLevels().size());
    }

    @Test
    void shouldAddSlotSuccessfully() {
        assertDoesNotThrow(() -> 
            lot.addLevel(1));

        SlotDto addedSlot = assertDoesNotThrow(() -> 
            lot.addSlot(1, SlotType.COMPACT));

        assertEquals(1, addedSlot.getId());
    }

    @Test
    void shouldAddSlotSequentiallySuccessfully() {
        assertDoesNotThrow(() -> 
            lot.addLevel(1));

        SlotDto addedSlot1 = assertDoesNotThrow(() -> 
            lot.addSlot(1, SlotType.COMPACT));
        SlotDto addedSlot2 = assertDoesNotThrow(() -> 
            lot.addSlot(1, SlotType.COMPACT));

        assertEquals(1, addedSlot1.getId());
        assertEquals(2, addedSlot2.getId());
    }

    @Test
    void shouldOccupyAndReleaseSlotSuccessfully() {
        assertDoesNotThrow(() -> 
            lot.addLevel(1));
        SlotDto slot = assertDoesNotThrow(() -> 
            lot.addSlot(1, SlotType.COMPACT));
        SlotAssignment assignment = new SlotAssignment(lot.getId(), 1, slot.getId(), SlotType.COMPACT);

        assertDoesNotThrow(() -> 
            lot.occupySlot(assignment));
        
        assertThat(lot.hasOccupiedSlots());

        Throwable ex2 = assertThrows(RestrictedLotOperationException.class,
            () -> lot.removeSlot(1, slot.getId()));
        assertEquals("Slot with id [1] in level [1] is occupied and cannot be removed", ex2.getMessage());

        assertDoesNotThrow(() -> 
            lot.releaseSlot(assignment));

        assertDoesNotThrow(() -> lot.removeSlot(1, slot.getId()));
    }

    @Test
    void shouldRemoveEmptyLevel() {
        assertDoesNotThrow(() -> 
            lot.addLevel(1));

        assertDoesNotThrow(() -> 
            lot.removeLevel(1));

        assertEquals(0, lot.getLevels().size());
    }

    @Test
    void shouldThrowWhenAddingDuplicateLevel() {
        assertDoesNotThrow(() -> 
            lot.addLevel(1));

        Throwable ex = assertThrows(ConflictException.class,
            () -> lot.addLevel(1));
        assertEquals("Level with number [1] already exists in lot [Test Lot]", ex.getMessage());
    }

    @Test
    void shouldPreventRemovingLevelWithOccupiedSlots() {
        assertDoesNotThrow(() -> 
            lot.addLevel(1));
        SlotDto slot = assertDoesNotThrow(() -> 
            lot.addSlot(1, SlotType.COMPACT));
        SlotAssignment assignment = new SlotAssignment(lot.getId(), 1, slot.getId(), SlotType.COMPACT);

        assertDoesNotThrow(() -> 
            lot.occupySlot(assignment));

        Throwable ex = assertThrows(RestrictedLotOperationException.class,
            () -> lot.removeLevel(1));
    }

    @Test
    void shouldThrowWhenLevelNotFound() {
        Throwable ex = assertThrows(LevelNotFoundException.class,
            () -> lot.addSlot(999, SlotType.COMPACT));
    }

    @Test
    void shouldThrowWhenSlotNotFound() {
        assertDoesNotThrow(() -> 
            lot.addLevel(1));
        SlotAssignment assignment = new SlotAssignment(lot.getId(), 1, 999, SlotType.COMPACT);

        Throwable ex = assertThrows(SlotNotFoundException.class,
            () -> lot.occupySlot(assignment));
    }

    @Test
    void shouldThrowWhenRemovingMissingLevel() {
        Throwable ex = assertThrows(LevelNotFoundException.class,
            () -> lot.removeLevel(1));
    }

    @Test
    void shouldMaintainConsistencyAfterOccupyReleaseCycle() {
        assertDoesNotThrow(() -> 
            lot.addLevel(1));
        SlotDto slot = assertDoesNotThrow(() -> 
            lot.addSlot(1, SlotType.COMPACT));
        SlotAssignment assignment = new SlotAssignment(lot.getId(), 1, slot.getId(), SlotType.COMPACT);
        
        assertDoesNotThrow(() -> 
            lot.occupySlot(assignment));
        assertDoesNotThrow(() -> 
            lot.releaseSlot(assignment));

        assertDoesNotThrow(() -> lot.removeSlot(1, slot.getId()));
    }

    @Test
    void shouldHandleSlotStatusTransitions() {
        assertDoesNotThrow(() -> 
            lot.addLevel(1));
        SlotDto slot = assertDoesNotThrow(() -> 
            lot.addSlot(1, SlotType.COMPACT));
        SlotAssignment assignment = new SlotAssignment(lot.getId(), 1, slot.getId(), SlotType.COMPACT);
        
        assertDoesNotThrow(() -> 
            lot.changeSlotStatus(1, slot.getId(), SlotStatus.UNAVAILABLE));
        assertDoesNotThrow(() -> 
            lot.changeSlotStatus(1, slot.getId(), SlotStatus.AVAILABLE));

        assertDoesNotThrow(() -> lot.occupySlot(assignment));
    }

    @Test
    void shouldNotAllowRemovingOccupiedSlot() {
        assertDoesNotThrow(() -> 
            lot.addLevel(1));
        SlotDto slot = assertDoesNotThrow(() -> 
            lot.addSlot(1, SlotType.COMPACT));
        SlotAssignment assignment = new SlotAssignment(lot.getId(), 1, slot.getId(), SlotType.COMPACT);

        assertDoesNotThrow(() -> 
            lot.occupySlot(assignment));

        Throwable ex = assertThrows(RestrictedLotOperationException.class,
            () -> lot.removeSlot(1, slot.getId()));
    }
}
