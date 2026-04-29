package org.test.parking.service.slot;

import org.test.parking.domain.model.session.SlotAssignment;
import org.test.parking.domain.model.space.Lot;
import org.test.parking.domain.model.vehicle.Vehicle;
import org.test.parking.exception.domain.NoAvailableSlotsException;

/**
 * Service responsible for assigning parking slots to vehicles.
 * <p>
 * Determines a suitable available slot based on vehicle type
 * and parking lot constraints.
 * </p>
 */
public interface SlotAssignmentService {

    //mutates Slot within the Lot aggregate, so it should be transactional and handle concurrency at the aggregate level
    /**
     * Assigns a parking slot to the given vehicle.
     *
     * @param lot     parking lot aggregate
     * @param vehicle vehicle requesting a slot
     * @return slot assignment result
     * @throws NoAvailableSlotsException if no suitable slot is found
     */
    SlotAssignment assignSlot(Lot lot, Vehicle vehicle)
        throws NoAvailableSlotsException;
}
