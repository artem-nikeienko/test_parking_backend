package org.test.parking.slot.impl;

import org.test.parking.domain.space.Slot;
import org.test.parking.domain.vehicle.Vehicle;
import org.test.parking.slot.SlotAllocationStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FirstFitSlotAllocationStrategy implements SlotAllocationStrategy {
    @Override
    public Slot select(List<Slot> available, Vehicle vehicle) {
        if (available.isEmpty()) return null;
        return available.get(0);
    }
}
