package org.test.parking.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.test.parking.domain.model.session.SlotAssignment;
import org.test.parking.domain.model.space.Lot;
import org.test.parking.domain.model.vehicle.Vehicle;
import org.test.parking.exception.NoAvailableSlotsException;
import org.test.parking.exception.domain.LevelNotFoundException;
import org.test.parking.exception.domain.RestrictedSlotOperationException;
import org.test.parking.exception.domain.SlotNotFoundException;
import org.test.parking.service.SlotAssignmentService;
import org.test.parking.service.SlotQueryService;

@Service
public class SlotAssignmentServiceImpl implements SlotAssignmentService {

    private final SlotQueryService slotQueryService;

    public SlotAssignmentServiceImpl(
        SlotQueryService slotQueryService
    ) {
        this.slotQueryService = slotQueryService;
    }

    @Override
    @Transactional
    public SlotAssignment assignSlot(Lot lot, Vehicle vehicle)
            throws NoAvailableSlotsException {

        Optional<SlotAssignment> optSlotAssignment = slotQueryService.findBestSlot(lot, vehicle);
        if (optSlotAssignment.isEmpty()) {
            throw new NoAvailableSlotsException(
                String.format("No available slot for [%s] vehicle type", vehicle.getType())
            );
        }
        
        SlotAssignment slotCandidate = optSlotAssignment.get();
        try {
            lot.occupySlot(slotCandidate.getLevelNumber(), slotCandidate.getSlotId()); 
        } catch (LevelNotFoundException | SlotNotFoundException | RestrictedSlotOperationException e) {
            // This should not happen since we just selected the slot, but in case it does, we can wrap and rethrow as a runtime exception
            throw new NoAvailableSlotsException(String.format("Unexpected error while occupying the slot. Message: [%s]", e.getMessage()));
        }

        return slotCandidate;
    }
}
