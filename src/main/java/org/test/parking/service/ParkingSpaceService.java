package org.test.parking.service;

import java.util.Optional;

import org.test.parking.domain.session.SlotStatus;
import org.test.parking.domain.space.Level;
import org.test.parking.domain.space.Lot;
import org.test.parking.domain.space.Slot;
import org.test.parking.domain.space.SlotType;
import org.test.parking.exception.ConflictException;
import org.test.parking.exception.NotFoundException;

public interface ParkingSpaceService {
    
    Lot addLot(String lotName) throws ConflictException;
    
    void removeLot(String id);

    Optional<Level> addLevel(String lotId, int levelNumber) throws NotFoundException;
    
    Optional<Level> removeLevel(String lotId, int levelNumber) throws NotFoundException;

    Slot addSlot(String lotId, int levelNumber, SlotType slotType) throws NotFoundException;
    
    Optional<Slot> changeSlotStatus(String lotId, int levelNumber, int slotId, SlotStatus slotStatus) throws NotFoundException;
    
    Optional<Slot> removeSlot(String lotId, int levelNumber, int slotId) throws NotFoundException;
}