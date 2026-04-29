package org.test.parking.domain.policy.slot;

import org.test.parking.domain.model.space.Slot;
import org.test.parking.domain.model.vehicle.Vehicle;

/**
 * Domain policy that determines whether a parking slot
 * is available for assignment.
 *
 * <p>Encapsulates rules such as:
 * - current occupancy
 * - maintenance status
 * - temporary restrictions</p>
 */
public interface SlotAvailabilityPolicy {

    //ASSUMPTION: This policy may be extended to let some vehicles be able to park in occupied or unavailable slots, for example, if the slot is occupied by a motorcycle and the vehicle is another bike, it may be allowed to park there.
    /**
     * Determines whether the given slot can be used by the specified vehicle.
     *
     * @param slot    parking slot to evaluate
     * @param vehicle vehicle requesting the slot
     * @return true if the slot is considered available, false otherwise
     */
    boolean isAvailable(Slot slot, Vehicle vehicle);
}
