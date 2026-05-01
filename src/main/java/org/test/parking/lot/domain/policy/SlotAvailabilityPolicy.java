package org.test.parking.lot.domain.policy;

import org.test.parking.lot.domain.SlotDto;
import org.test.parking.vehicle.domain.Vehicle;

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

    /**
     * Determines whether the given slot can be used by the specified vehicle.
     *
     * @param slot    parking slot to evaluate
     * @param vehicle vehicle requesting the slot
     * @return true if the slot is considered available, false otherwise
     */
    boolean isAvailable(SlotDto slot, Vehicle vehicle);
}
