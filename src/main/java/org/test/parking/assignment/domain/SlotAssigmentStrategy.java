package org.test.parking.assignment.domain;

import org.test.parking.lot.domain.Slot;
import org.test.parking.vehicle.domain.Vehicle;

public interface SlotAssigmentStrategy {
    
/**
 * Determines whether the given candidate slot is a better choice than the current best slot
 * for allocating a parking space to the specified vehicle.
 *
 * <p>This method is used by the {@code Lot} aggregate during slot selection. The aggregate
 * is responsible for iterating over eligible slots and delegates only the comparison logic
 * to the strategy.</p>
 *
 * @param candidate   A candidate slot currently being evaluated. Never null.
 * @param currentBest The best slot found so far. May be null if no slot has been selected yet.
 * @param vehicle     The vehicle requesting a parking slot. Never null.
 *
 * @return {@code true} if the candidate slot should replace the current best slot;
 *         {@code false} otherwise.
 *
 * @throws IllegalArgumentException if candidate or vehicle is null.
 *
 * Notes:
 * - Implementations should define comparison rules only (e.g., first-fit, nearest, priority-based).
 * - Slot eligibility (availability, compatibility with vehicle type) is guaranteed by the caller
 *   and must not be revalidated here.
 * - The strategy must be side-effect free and should not modify slot state.
 * - This method does not perform selection directly, but participates in a selection process
 *   controlled by the aggregate.
 */
    boolean isBetter(Slot candidate, Slot currentBest, Vehicle vehicle);
}
