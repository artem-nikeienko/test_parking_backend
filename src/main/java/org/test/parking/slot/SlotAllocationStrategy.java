package org.test.parking.slot;

import org.test.parking.domain.space.Slot;
import org.test.parking.domain.vehicle.Vehicle;

import java.util.List;

//TODO: See if we can move '/fee' and '/slot' to '/service'
public interface SlotAllocationStrategy {
    Slot select(List<Slot> available, Vehicle vehicle);
}
