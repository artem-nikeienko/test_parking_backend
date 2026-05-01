package org.test.parking.assignment.domain.impl;

import org.springframework.stereotype.Component;
import org.test.parking.assignment.domain.SlotAssigmentStrategy;
import org.test.parking.lot.domain.SlotDto;
import org.test.parking.vehicle.domain.Vehicle;

@Component
public class FirstFitSlotAssignmentStrategy implements SlotAssigmentStrategy {
    
    @Override
    public boolean isBetter(SlotDto candidate, SlotDto currentBest, Vehicle vehicle) {
        return currentBest == null;
    }
}
