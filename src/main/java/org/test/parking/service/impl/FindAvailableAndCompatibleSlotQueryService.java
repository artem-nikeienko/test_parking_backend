package org.test.parking.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.test.parking.domain.model.session.SlotAssignment;
import org.test.parking.domain.model.space.Level;
import org.test.parking.domain.model.space.Lot;
import org.test.parking.domain.model.space.Slot;
import org.test.parking.domain.model.vehicle.Vehicle;
import org.test.parking.domain.policy.SlotAvailabilityPolicy;
import org.test.parking.domain.policy.SlotCompatibilityPolicy;
import org.test.parking.service.SlotQueryService;
import org.test.parking.slot.SlotAssigmentStrategy;

@Component
public class FindAvailableAndCompatibleSlotQueryService implements SlotQueryService {

    private final SlotAssigmentStrategy strategy;
    private final SlotCompatibilityPolicy compatibilityPolicy;
    private final SlotAvailabilityPolicy availabilityPolicy;

    public FindAvailableAndCompatibleSlotQueryService(
        SlotAssigmentStrategy strategy,
        SlotCompatibilityPolicy compatibilityPolicy,
        SlotAvailabilityPolicy availabilityPolicy
    ) {
        this.strategy = strategy;
        this.compatibilityPolicy = compatibilityPolicy;
        this.availabilityPolicy = availabilityPolicy;
    }

    @Override
    //TODO: try to avoid of iterating over aggregate by getting its components. Maybe use ligtweight read models for that, with ObjectValue as ID instead of Slot itself
    public Optional<SlotAssignment> findBestSlot(Lot lot, Vehicle vehicle) {
        Slot best = null;
        //ASSUMPTION: Here we do not short circuit when we find the first available and compatible slot
        //  because we want to apply the strategy to find the best one,
        //  for example, the closest one to the entrance,
        //  or the one with the best lighting, etc.
        //  If we short circuit, we would just return the first one we find,
        //  which may not be the best one according to the strategy.
        
        for (Level level : lot.getLevels()) {
            for (Slot slot : level.getSlots()) {
                if (availabilityPolicy.isAvailable(slot, vehicle) &&
                    compatibilityPolicy.isCompatible(slot, vehicle) &&
                    strategy.isBetter(slot, best, vehicle)
                ) {
                    best = slot;
                }
            }
        }
        return best == null ? Optional.empty() : Optional.of(new SlotAssignment(best.getLevelNumber(), best.getId()));
    }
}
