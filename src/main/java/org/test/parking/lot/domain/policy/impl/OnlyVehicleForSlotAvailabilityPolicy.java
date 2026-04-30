package org.test.parking.lot.domain.policy.impl;

import org.springframework.stereotype.Component;
import org.test.parking.lot.domain.Slot;
import org.test.parking.lot.domain.policy.SlotAvailabilityPolicy;
import org.test.parking.vehicle.domain.Vehicle;

@Component
public class OnlyVehicleForSlotAvailabilityPolicy implements SlotAvailabilityPolicy {

    @Override
    public boolean isAvailable(Slot slot, Vehicle vehicle) {
        return !(slot.isOccupied() || slot.isUnavailable());
    }
}
