package org.test.parking.lot.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.List;

import org.test.parking.exception.domain.RestrictedLotOperationException;
import org.test.parking.exception.domain.SlotNotFoundException;
import org.test.parking.session.domain.SlotStatus;

public class Level {

    private int slotCounter;

    private final int number;
    private final String lotId;
    private final Map<Integer, Slot> slots = new HashMap<>();

    protected Level(int number, String lotId) {
        this.number = number;
        this.lotId = lotId;
        this.slotCounter = 0;
    }

    protected int getNumber() {
        return number;
    }

    protected String getLotId() {
        return lotId;
    }

    protected SlotDto addSlot(SlotType slotType) {
        int newSlotId = ++slotCounter;
        Slot slot = new Slot(newSlotId, number, slotType);
        slots.put(slot.getId(), slot);
        return SlotDto.from(slot);
    }

    protected SlotDto removeSlot(int slotId) throws SlotNotFoundException, RestrictedLotOperationException{
        Slot slot = getSlotOrThrow(slotId);
        if (slot.isOccupied()) {
            throw new RestrictedLotOperationException(String.format("Slot with id [%d] in level [%d] is occupied and cannot be removed", slotId, number));
        }
        Slot removedSlot = slots.remove(slotId);
        return SlotDto.from(removedSlot);
    }

    protected void occupySlot(int slotId) throws SlotNotFoundException, RestrictedLotOperationException {
        getSlotOrThrow(slotId)
            .occupy();
    }

    protected void releaseSlot(int slotId) throws SlotNotFoundException, RestrictedLotOperationException {
        getSlotOrThrow(slotId)
            .release();
    }

    protected SlotDto changeSlotStatus(Integer slotId, SlotStatus slotStatus) throws SlotNotFoundException, RestrictedLotOperationException {
        Slot slot = getSlotOrThrow(slotId);
        switch (slotStatus) {
            case AVAILABLE -> slot.release();
            case OCCUPIED -> slot.occupy();
            case UNAVAILABLE -> slot.markOutOfService();
        }
        return SlotDto.from(slot);
    }

    protected List<SlotDto> getSlots() {
        return slots.values().stream()
            .map(SlotDto::from)
            .sorted((a,b) -> a.getId() - b.getId())
            .collect(Collectors.toList());
    }

    protected boolean hasOccupiedSlots() {
        return slots.values().stream().anyMatch(Slot::isOccupied);
    }

    private Slot getSlotOrThrow(int slotId) throws SlotNotFoundException {
        Slot slot = slots.get(slotId);
        if (slot == null) {
            throw new SlotNotFoundException(String.format("Slot with id [%d] not found in level [%d]", slotId, number));
        }
        return slot;
    }
}
