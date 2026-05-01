package org.test.parking.assignment.service;

import org.test.parking.exception.domain.NoAvailableSlotsException;
import org.test.parking.lot.domain.Lot;
import org.test.parking.session.domain.SlotAssignment;
import org.test.parking.vehicle.domain.Vehicle;

/**
 * Service responsible for assigning parking slots to vehicles.
 * <p>
 * Determines a suitable available slot based on vehicle type
 * and parking lot constraints.
 * </p>
 */
public interface SlotAssignmentService {

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
