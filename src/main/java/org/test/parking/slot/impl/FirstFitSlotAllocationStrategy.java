package org.test.parking.slot.impl;

import java.util.List;

import org.springframework.stereotype.Component;
import org.test.parking.domain.space.Slot;
import org.test.parking.domain.vehicle.Vehicle;
import org.test.parking.slot.SlotAllocationStrategy;

@Component
public class FirstFitSlotAllocationStrategy implements SlotAllocationStrategy {
    
    @Override
    public Slot select(List<Slot> available, Vehicle vehicle) {
        if (available.isEmpty()) return null;
        return available.get(0);
    }
}
