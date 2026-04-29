package org.test.parking.domain.strategy.slot.impl;

import org.springframework.stereotype.Component;
import org.test.parking.domain.model.space.Slot;
import org.test.parking.domain.model.vehicle.Vehicle;
import org.test.parking.domain.strategy.slot.SlotAssigmentStrategy;

@Component
public class FirstFitSlotAssignmentStrategy implements SlotAssigmentStrategy {
    
    @Override
    public boolean isBetter(Slot candidate, Slot currentBest, Vehicle vehicle) {
        return currentBest == null;
    }
}
