package org.test.parking.domain.space;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.test.parking.domain.SlotAssignment;
import org.test.parking.domain.session.SlotStatus;
import org.test.parking.domain.vehicle.Vehicle;
import org.test.parking.exception.NotFoundException;
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

    public Level removeLevel(int levelNumber) {
        Level removedLevel = levels.remove(levelNumber);
        return removedLevel;
    }

    public Optional<SlotAssignment> assignSlot(Vehicle vehicle) {
        for (Level level : levels.values()) {
            Optional<Slot> slot = level.findAvailableSlot(vehicle);
            if (slot.isPresent()) {
                Slot s = slot.get();
                s.occupy();
                return Optional.of(new SlotAssignment(level, s));
            }
        }
        return Optional.empty();
    }

    public String getId() {
        return id;
    }

    public Slot addSlot(int levelNumber, SlotType slotType) throws NotFoundException {
        Level level = getLevelOrThrow(levelNumber);
        return level.addSlot(slotType);
    }

    public Slot changeSlotStatus(int levelNumber, int slotId, SlotStatus slotStatus) throws NotFoundException {
        Level level = getLevelOrThrow(levelNumber);
        return level.changeSlotStatus(slotId, slotStatus);
    }

    public Slot removeSlot(int levelNumber, int slotId) throws NotFoundException {
        Level level = getLevelOrThrow(levelNumber);
        return level.removeSlot(slotId);
    }

    public Optional<Slot> allocateSlot(Vehicle vehicle, SlotAllocationStrategy strategy) {
        List<Slot> candidates = findAvailableSlots(vehicle);
    
        Slot selected = strategy.select(candidates, vehicle);
    
        if (selected == null) {
            return Optional.empty();
        }
    
        selected.occupy();
    
        return Optional.of(selected);
    }

    public void releaseSlot(int levelNumber, int slotId) throws NotFoundException {
        Level level = getLevelOrThrow(levelNumber);
        level.changeSlotStatus(slotId, SlotStatus.AVAILABLE);
    }

    private Level getLevelOrThrow(int levelNumber) throws NotFoundException {
        Level level = levels.get(levelNumber);
        if (level == null) {
            throw new NotFoundException(String.format("Level with number %n not found", levelNumber));
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
}
