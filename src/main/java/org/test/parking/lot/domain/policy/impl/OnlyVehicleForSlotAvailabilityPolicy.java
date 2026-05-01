package org.test.parking.lot.domain.policy.impl;

import org.springframework.stereotype.Component;
import org.test.parking.lot.domain.SlotDto;
import org.test.parking.lot.domain.policy.SlotAvailabilityPolicy;
import org.test.parking.session.domain.SlotStatus;
import org.test.parking.vehicle.domain.Vehicle;

@Component
public class OnlyVehicleForSlotAvailabilityPolicy implements SlotAvailabilityPolicy {

    @Override
    public boolean isAvailable(SlotDto slot, Vehicle vehicle) {
        return slot.getStatus() == SlotStatus.AVAILABLE;
    }
}
