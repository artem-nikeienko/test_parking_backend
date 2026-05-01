package org.test.parking.assignment.query.impl;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.test.parking.assignment.domain.SlotAssigmentStrategy;
import org.test.parking.assignment.query.SlotQueryService;
import org.test.parking.lot.domain.LevelDto;
import org.test.parking.lot.domain.Lot;
import org.test.parking.lot.domain.SlotDto;
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
    public Optional<SlotAssignment> findBestSlot(Lot lot, Vehicle vehicle) {
        SlotDto best = null;

        for (LevelDto level : lot.getLevels()) {
            for (SlotDto slot : level.getSlots()) {
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
