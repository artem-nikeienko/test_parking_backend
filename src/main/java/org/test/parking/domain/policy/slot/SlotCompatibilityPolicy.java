package org.test.parking.domain.policy.slot;

import org.test.parking.domain.model.space.Slot;
import org.test.parking.domain.model.vehicle.Vehicle;

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
    boolean isCompatible(Slot slot, Vehicle vehicle);
}
