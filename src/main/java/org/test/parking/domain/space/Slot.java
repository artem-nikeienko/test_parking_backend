package org.test.parking.domain.space;

import java.util.EnumSet;
import java.util.Set;

import org.test.parking.domain.session.SlotStatus;
import org.test.parking.domain.vehicle.Vehicle;
import org.test.parking.domain.vehicle.VehicleType;
import org.test.parking.exception.domain.RestrictedSlotOperationException;

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

    public boolean isAvailable() {
        return status == SlotStatus.AVAILABLE;
    }

    protected void occupy() throws RestrictedSlotOperationException {
        if (!isAvailable()) {
            throw new RestrictedSlotOperationException(String.format("Cannot occupy slot [%d] in level [%d] because it is currently %s", id, levelNumber, status));
        }
        this.status = SlotStatus.OCCUPIED;
    }

    public boolean isOccupied() {
        return status == SlotStatus.OCCUPIED;
    }

    protected void release() {
        this.status = SlotStatus.AVAILABLE;
    }

    protected void markOutOfService() throws RestrictedSlotOperationException {
        if (isOccupied()) {
            throw new RestrictedSlotOperationException(String.format("Cannot mark slot [%d] in level [%d] as out of service because it is currently occupied", id, levelNumber));
        }
        this.status = SlotStatus.UNAVAILABLE;
    }

    public SlotType getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public boolean isCompatibleWith(Vehicle vehicle) {
        Set<SlotType> compatibleTypes = compatible(vehicle.getType());
        return compatibleTypes.contains(type);
    }

    private Set<SlotType> compatible(VehicleType t) {
        return switch (t) {
            case MOTORCYCLE -> EnumSet.of(SlotType.MOTORCYCLE, SlotType.COMPACT);
            case CAR -> EnumSet.of(SlotType.COMPACT, SlotType.LARGE, SlotType.HANDICAPPED);
            case TRUCK -> EnumSet.of(SlotType.LARGE);
            default -> EnumSet.allOf(SlotType.class);
        };
    }
}
