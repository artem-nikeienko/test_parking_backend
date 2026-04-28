package org.test.parking.domain.space;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.test.parking.domain.SlotAssignment;
import org.test.parking.domain.session.SlotStatus;
import org.test.parking.domain.vehicle.Vehicle;
import org.test.parking.exception.LotFullException;
import org.test.parking.exception.domain.LevelNotFoundException;
import org.test.parking.exception.domain.RestrictedSlotOperationException;
import org.test.parking.exception.domain.SlotNotFoundException;
import org.test.parking.slot.SlotAllocationStrategy;

import lombok.Builder;

@Builder
public class Lot {

    private final String id;
    private final String name;
    private final HashMap<Integer, Level> levels = new HashMap<>();

    protected Lot(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Lot(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Level addLevel(int levelNumber) {
        if(levels.containsKey(levelNumber)) {
            return null;
        }
        Level addedLevel = levels.put(levelNumber, new Level(levelNumber));
        //ASSUMPTION: I separate operation result and its return value to have space for future extension, e.g. logging, event emitting, etc.
        return addedLevel;
    }

    public Level removeLevel(int levelNumber) throws RestrictedSlotOperationException{
        if (hasOccupiedSlots(levelNumber)) {
            throw new RestrictedSlotOperationException(String.format("Level [%d] in lot [%s] contains occupied slots and cannot be removed", levelNumber, name));
        }
        Level removedLevel = levels.remove(levelNumber);
        return removedLevel;
    }

    public String getId() {
        return id;
    }

    public Slot addSlot(int levelNumber, SlotType slotType) throws LevelNotFoundException {
        Level level = getLevelOrThrow(levelNumber);
        return level.addSlot(slotType);
    }

    public Slot changeSlotStatus(int levelNumber, int slotId, SlotStatus slotStatus) throws LevelNotFoundException, SlotNotFoundException, RestrictedSlotOperationException {
        Level level = getLevelOrThrow(levelNumber);
        return level.changeSlotStatus(slotId, slotStatus);
    }

    public Slot removeSlot(int levelNumber, int slotId) throws LevelNotFoundException, SlotNotFoundException, RestrictedSlotOperationException {
        Level level = getLevelOrThrow(levelNumber);
        return level.removeSlot(slotId);
    }

    public Optional<Slot> allocateSlot(Vehicle vehicle, SlotAllocationStrategy strategy) throws LotFullException, RestrictedSlotOperationException {
        if (isFull()) {
            throw new LotFullException("No available slot");
        }
        
        List<Slot> candidates = findAvailableSlots(vehicle);
        Slot selected = strategy.select(candidates, vehicle);
        if (selected == null) {
            return Optional.empty();
        }
        selected.occupy();
    
        return Optional.of(selected);
    }

    public void releaseSlot(Slot slot) {
        slot.release();
    }

    private Level getLevelOrThrow(int levelNumber) throws LevelNotFoundException {
        Level level = levels.get(levelNumber);
        if (level == null) {
            throw new LevelNotFoundException(String.format("Level with number %n not found", levelNumber));
        }
        return level;
    }

    private List<Slot> findAvailableSlots(Vehicle vehicle) {
        return levels.values().stream()
            .flatMap(level -> level.getSlots().stream())
            .filter(slot -> slot.isAvailable())
            .filter(slot -> slot.isCompatibleWith(vehicle))
            .toList();
    }

    public boolean isFull() {
        return levels.values().stream()
            .flatMap(level -> level.getSlots().stream())
            .noneMatch(slot -> slot.isAvailable());
    }

    private boolean hasOccupiedSlots(int levelNumber) {
        return levels.get(levelNumber).getSlots().stream()
            .anyMatch(slot -> slot.isOccupied());
    }
}
