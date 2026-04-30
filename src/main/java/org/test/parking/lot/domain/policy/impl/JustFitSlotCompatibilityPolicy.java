package org.test.parking.lot.domain.policy.impl;

import org.springframework.stereotype.Component;
import org.test.parking.lot.domain.Slot;
import org.test.parking.lot.domain.SlotType;
import org.test.parking.lot.domain.policy.SlotCompatibilityPolicy;
import org.test.parking.vehicle.domain.Vehicle;

@Component
public class JustFitSlotCompatibilityPolicy implements SlotCompatibilityPolicy {

    @Override
    public boolean isCompatible(Slot slot, Vehicle vehicle) {
        return switch (vehicle.getType()) {
            case MOTORCYCLE -> true;
            case CAR -> slot.getType() != SlotType.MOTORCYCLE;
            case TRUCK -> slot.getType() == SlotType.LARGE;
        };
    }
}
