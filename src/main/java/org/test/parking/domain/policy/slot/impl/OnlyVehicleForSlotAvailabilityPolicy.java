package org.test.parking.domain.policy.slot.impl;

import org.springframework.stereotype.Component;
import org.test.parking.domain.model.space.Slot;
import org.test.parking.domain.model.vehicle.Vehicle;
import org.test.parking.domain.policy.slot.SlotAvailabilityPolicy;

@Component
public class OnlyVehicleForSlotAvailabilityPolicy implements SlotAvailabilityPolicy {

    @Override
    public boolean isAvailable(Slot slot, Vehicle vehicle) {
        return !(slot.isOccupied() || slot.isUnavailable());
    }
}
