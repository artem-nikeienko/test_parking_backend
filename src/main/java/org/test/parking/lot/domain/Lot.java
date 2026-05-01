package org.test.parking.lot.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.test.parking.exception.domain.ConflictException;
import org.test.parking.exception.domain.LevelNotFoundException;
import org.test.parking.exception.domain.RestrictedLotOperationException;
import org.test.parking.exception.domain.SlotNotFoundException;
import org.test.parking.session.domain.SlotAssignment;
import org.test.parking.session.domain.SlotStatus;

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

    public List<LevelDto> getLevels() {
        return levels.values().stream()
            .map(LevelDto::from)
            .sorted((a,b) -> a.getNumber() - b.getNumber())
            .collect(Collectors.toList());
    }

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

    public LevelDto addLevel(int levelNumber) throws ConflictException {
        if(levels.containsKey(levelNumber)) {
            throw new ConflictException(String.format("Level with number [%d] already exists in lot [%s]", levelNumber, name));
        }
        Level newLevel = new Level(levelNumber, id);
        levels.put(levelNumber, newLevel);
        return LevelDto.from(newLevel);
    }

    public LevelDto removeLevel(int levelNumber) throws LevelNotFoundException, RestrictedLotOperationException {
        Level level = getLevelOrThrow(levelNumber);
        if (level.hasOccupiedSlots()) {
            throw new RestrictedLotOperationException(String.format("Level [%d] in lot [%s] contains occupied slots and cannot be removed", levelNumber, name));
        }
        Level removedLevel = levels.remove(levelNumber);
        return LevelDto.from(removedLevel);
    }

    public SlotDto addSlot(int levelNumber, SlotType slotType) throws LevelNotFoundException {
        SlotDto addedSlot = getLevelOrThrow(levelNumber)
            .addSlot(slotType);
        return addedSlot;
    }

    public SlotDto changeSlotStatus(int levelNumber, int slotId, SlotStatus slotStatus) throws LevelNotFoundException, SlotNotFoundException, RestrictedLotOperationException {
        return getLevelOrThrow(levelNumber)
            .changeSlotStatus(slotId, slotStatus);
    }

    public SlotDto removeSlot(int levelNumber, int slotId) throws LevelNotFoundException, SlotNotFoundException, RestrictedLotOperationException {
        return getLevelOrThrow(levelNumber)
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
