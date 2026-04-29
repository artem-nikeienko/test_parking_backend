package org.test.parking.domain.model.space;

import org.test.parking.domain.model.session.SlotStatus;
import org.test.parking.exception.domain.RestrictedLotOperationException;

import lombok.Getter;

@Getter
public class Slot {
    //TODO: Provide Value Objects as IDs for better type safety and encapsulation, e.g. SlotId, LevelId, etc.
    private final int id;
    private final int levelNumber;
    private final SlotType type;
    private SlotStatus status;

    public Slot(int id, int levelNumber, SlotType slotType) {
        this.id = id;
        this.levelNumber = levelNumber;
        this.type = slotType;
        this.status = SlotStatus.AVAILABLE;
    }

    protected void occupy() throws RestrictedLotOperationException {
        if (isUnavailable()) {
            throw new RestrictedLotOperationException(String.format("Cannot occupy slot [%d] in level [%d] because it is currently %s", id, levelNumber, status));
        }
        status = SlotStatus.OCCUPIED;
    }

    protected void release() throws RestrictedLotOperationException{
        if (!isUnavailable()) {
            throw new RestrictedLotOperationException(String.format("Cannot release slot [%d] in level [%d] because it is currently [%s]", id, levelNumber, status));
        }
        status = SlotStatus.AVAILABLE;
    }

    protected void markOutOfService() throws RestrictedLotOperationException {
        if (isOccupied()) {
            throw new RestrictedLotOperationException(String.format("Cannot mark slot [%d] in level [%d] as out of service because it is currently occupied", id, levelNumber));
        }
        status = SlotStatus.UNAVAILABLE;
    }

    public boolean isUnavailable() {
        return status != SlotStatus.AVAILABLE;
    }

    public boolean isOccupied() {
        return status == SlotStatus.OCCUPIED;
    }
}
