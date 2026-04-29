package org.test.parking.domain.policy;

import org.test.parking.domain.model.space.Slot;
import org.test.parking.domain.model.vehicle.Vehicle;

public interface SlotCompatibilityPolicy {

    boolean isCompatible(Slot slot, Vehicle vehicle);
}
