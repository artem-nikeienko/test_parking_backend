package org.test.parking.domain.model.space;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.test.parking.domain.model.session.SlotAssignment;
import org.test.parking.domain.model.session.SlotStatus;
import org.test.parking.exception.ConflictException;
import org.test.parking.exception.domain.LevelNotFoundException;
import org.test.parking.exception.domain.RestrictedLotOperationException;
import org.test.parking.exception.domain.SlotNotFoundException;

public class Lot {

    private final String id;
    private final String name;
    private final Map<Integer, Level> levels = new HashMap<>();

    public Lot(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Collection<Level> getLevels() {
        return levels.values();
    }

    //TODO: add tests for this
    public boolean hasOccupiedSlots() {
        return levels.values().stream().anyMatch(Level::hasOccupiedSlots);
    }

    public void occupySlot(SlotAssignment slotAssignment) throws LevelNotFoundException, SlotNotFoundException, RestrictedLotOperationException {
        getLevelOrThrow(slotAssignment.getLevelNumber())
            .occupySlot(slotAssignment.getSlotId());
    }

    public void releaseSlot(SlotAssignment slotAssignment) throws LevelNotFoundException, SlotNotFoundException, RestrictedLotOperationException {
        getLevelOrThrow(slotAssignment.getLevelNumber())
            .releaseSlot(slotAssignment.getSlotId());
    }

    public int addLevel(int levelNumber) throws ConflictException {
        if(levels.containsKey(levelNumber)) {
            throw new ConflictException(String.format("Level with number [%d] already exists in lot [%s]", levelNumber, name));
        }
        Level newLevel = new Level(levelNumber);
        levels.put(levelNumber, newLevel);
        return newLevel.getNumber();
    }

    public void removeLevel(int levelNumber) throws LevelNotFoundException, RestrictedLotOperationException {
        Level level = getLevelOrThrow(levelNumber);
        if (level.hasOccupiedSlots()) {
            throw new RestrictedLotOperationException(String.format("Level [%d] in lot [%s] contains occupied slots and cannot be removed", levelNumber, name));
        }
        levels.remove(levelNumber);
    }

    public int addSlot(int levelNumber, SlotType slotType) throws LevelNotFoundException {
        return getLevelOrThrow(levelNumber)
            .addSlot(slotType);
    }

    public void changeSlotStatus(int levelNumber, int slotId, SlotStatus slotStatus) throws LevelNotFoundException, SlotNotFoundException, RestrictedLotOperationException {
        getLevelOrThrow(levelNumber)
            .changeSlotStatus(slotId, slotStatus);
    }

    public void removeSlot(int levelNumber, int slotId) throws LevelNotFoundException, SlotNotFoundException, RestrictedLotOperationException {
        getLevelOrThrow(levelNumber)
            .removeSlot(slotId);
    }

    private Level getLevelOrThrow(int levelNumber) throws LevelNotFoundException {
        Level level = levels.get(levelNumber);
        if (level == null) {
            throw new LevelNotFoundException(String.format("Level with number [%d] not found", levelNumber));
        }
        return level;
    }
}
