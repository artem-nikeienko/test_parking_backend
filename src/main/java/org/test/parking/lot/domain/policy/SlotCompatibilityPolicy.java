package org.test.parking.lot.domain.policy;

import org.test.parking.lot.domain.SlotDto;
import org.test.parking.vehicle.domain.Vehicle;

/**
 * Policy that determines whether a given vehicle can occupy a specific slot.
 *
 * <p>Encapsulates domain rules for compatibility between
 * vehicle types and slot types.</p>
 */
public interface SlotCompatibilityPolicy {

    /**
     * Checks whether the vehicle is compatible with the slot.
     *
     * @param vehicle vehicle attempting to park
     * @param slot parking slot candidate
     * @return true if compatible, false otherwise
     */
    boolean isCompatible(SlotDto slot, Vehicle vehicle);
}
