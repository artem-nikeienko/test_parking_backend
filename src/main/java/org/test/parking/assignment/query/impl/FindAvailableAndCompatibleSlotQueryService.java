package org.test.parking.assignment.query.impl;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.test.parking.assignment.domain.SlotAssigmentStrategy;
import org.test.parking.assignment.query.SlotQueryService;
import org.test.parking.lot.domain.Level;
import org.test.parking.lot.domain.Lot;
import org.test.parking.lot.domain.Slot;
import org.test.parking.lot.domain.policy.SlotAvailabilityPolicy;
import org.test.parking.lot.domain.policy.SlotCompatibilityPolicy;
import org.test.parking.session.domain.SlotAssignment;
import org.test.parking.vehicle.domain.Vehicle;

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
        return best == null ? Optional.empty() : Optional.of(new SlotAssignment(lot.getId(), best.getLevelNumber(), best.getId(), best.getType()));
    }
}
