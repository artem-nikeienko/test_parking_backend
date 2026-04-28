package org.test.parking.slot;

import java.util.List;

import org.test.parking.domain.space.Slot;
import org.test.parking.domain.vehicle.Vehicle;

//TODO: See if we can move '/fee' and '/slot' to '/service'
//TODO: Add some more strategies based on level number etc.
public interface SlotAllocationStrategy {
    
    /**
     * Selects an appropriate parking slot for the given vehicle from a list of available slots.
     *
     * @param available List of available slots. Must not be null. May be empty.
     * @param vehicle   Vehicle requesting a parking slot. Must not be null.
     * @return Selected {@link Slot}, or null if no suitable slot is found.
     *
     * @throws IllegalArgumentException if available list or vehicle is null.
     *
     * Notes:
     * - Implementations must ensure compatibility between slot type and vehicle type.
     * - Strategy may apply additional rules (e.g., nearest slot, lowest level, priority slots).
     * - Returning null indicates that no suitable slot exists (e.g., lot is full or incompatible types).
     */
    Slot select(List<Slot> available, Vehicle vehicle);
}
