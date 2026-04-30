package org.test.parking.assignment.query;

import java.util.Optional;

import org.test.parking.lot.domain.Lot;
import org.test.parking.session.domain.SlotAssignment;
import org.test.parking.vehicle.domain.Vehicle;

/**
 * Provides read-only operations for querying suitable parking slots.
 */
public interface SlotQueryService {

    /**
     * Finds the best available slot for the given vehicle within the specified lot.
     *
     * @param lot     parking lot to search in
     * @param vehicle vehicle requesting a slot
     * @return optional containing best slot assignment if found
     */
    Optional<SlotAssignment> findBestSlot(Lot lot, Vehicle vehicle);
}
