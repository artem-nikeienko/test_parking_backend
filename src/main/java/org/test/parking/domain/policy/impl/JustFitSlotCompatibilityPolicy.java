package org.test.parking.domain.policy.impl;

import org.springframework.stereotype.Component;
import org.test.parking.domain.model.space.Slot;
import org.test.parking.domain.model.space.SlotType;
import org.test.parking.domain.model.vehicle.Vehicle;
import org.test.parking.domain.policy.SlotCompatibilityPolicy;

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
