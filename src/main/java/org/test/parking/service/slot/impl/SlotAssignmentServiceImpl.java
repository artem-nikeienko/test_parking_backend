package org.test.parking.service.slot.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.test.parking.domain.model.session.SlotAssignment;
import org.test.parking.domain.model.space.Lot;
import org.test.parking.domain.model.vehicle.Vehicle;
import org.test.parking.exception.domain.LevelNotFoundException;
import org.test.parking.exception.domain.NoAvailableSlotsException;
import org.test.parking.exception.domain.RestrictedLotOperationException;
import org.test.parking.exception.domain.SlotNotFoundException;
import org.test.parking.service.slot.SlotAssignmentService;
import org.test.parking.service.slot.SlotQueryService;

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

        //ASSUME: this synchronized for Lot Aggregate must be enough to guarantee thread-safe consistent behavior
        synchronized (lot) {
            
            Optional<SlotAssignment> optSlotAssignment = slotQueryService.findBestSlot(lot, vehicle);
            if (optSlotAssignment.isEmpty()) {
                throw new NoAvailableSlotsException(
                    String.format("No available slot for [%s] vehicle type", vehicle.getType())
                );
            }
            
            SlotAssignment slotAssignment = optSlotAssignment.get();
            try {
                lot.occupySlot(slotAssignment); 
            } catch (LevelNotFoundException | SlotNotFoundException | RestrictedLotOperationException e) {
                // This should not happen since we just selected the slot, but in case it does, we can wrap and rethrow as a runtime exception
                throw new NoAvailableSlotsException(String.format("Unexpected error while occupying the slot. Message: [%s]", e.getMessage()));
            }
    
            return slotAssignment;
        }
    }
}
