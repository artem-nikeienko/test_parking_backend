package org.test.parking.service;

import java.util.Optional;

import org.test.parking.domain.model.session.SlotAssignment;
import org.test.parking.domain.model.space.Lot;
import org.test.parking.domain.model.vehicle.Vehicle;

public interface SlotQueryService {

    Optional<SlotAssignment> findBestSlot(Lot lot, Vehicle vehicle);
}
