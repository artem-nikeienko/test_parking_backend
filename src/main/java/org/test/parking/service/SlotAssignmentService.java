package org.test.parking.service;

import org.test.parking.domain.model.session.SlotAssignment;
import org.test.parking.domain.model.space.Lot;
import org.test.parking.domain.model.vehicle.Vehicle;
import org.test.parking.exception.NoAvailableSlotsException;

public interface SlotAssignmentService {

    //mutates Slot within the Lot aggregate, so it should be transactional and handle concurrency at the aggregate level
    SlotAssignment assignSlot(Lot lot, Vehicle vehicle)
        throws NoAvailableSlotsException;

}
