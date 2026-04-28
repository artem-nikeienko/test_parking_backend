package org.test.parking.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.test.parking.domain.session.SlotStatus;
import org.test.parking.domain.space.Level;
import org.test.parking.domain.space.Lot;
import org.test.parking.domain.space.Slot;
import org.test.parking.domain.space.SlotType;
import org.test.parking.exception.ConflictException;
import org.test.parking.exception.NotFoundException;
import org.test.parking.repository.LotRepository;
import org.test.parking.service.ParkingSpaceService;

@Service
public class ParkingSpaceServiceImpl implements ParkingSpaceService {

    private final LotRepository repo;

    public ParkingSpaceServiceImpl(LotRepository repo) {
        this.repo = repo;
    }

    @Override
    public Lot addLot(String lotName) throws ConflictException {
        Optional<Lot> optLot = repo.findByName(lotName);
        if (optLot.isPresent()) {
            throw new ConflictException("Lot with the same name already exists");
        }
        Lot lot = new Lot(lotName);
        return repo.save(lot);
    }

    @Override
    public void removeLot(String id) {
        repo.delete(id);
    }

    @Override
    public Optional<Level> addLevel(String lotId, int levelNumber) throws NotFoundException {
        Lot lot = getLotOrThrow(lotId);
        Level addedLevel = lot.addLevel(levelNumber);
        return Optional.ofNullable(addedLevel);
    }

    @Override
    public Optional<Level> removeLevel(String lotId, int levelNumber) throws NotFoundException {
        Lot lot = getLotOrThrow(lotId);
        Level removedLevel = lot.removeLevel(levelNumber);
        return Optional.ofNullable(removedLevel);
    }

    @Override
    public Slot addSlot(String lotId, int levelNumber, SlotType slotType) throws NotFoundException {
        Lot lot = getLotOrThrow(lotId);
        Slot addedSlot = lot.addSlot(levelNumber, slotType);
        return addedSlot;
    }

    @Override
    public Optional<Slot> changeSlotStatus(String lotId, int levelNumber, int slotId, SlotStatus slotStatus) throws NotFoundException {
        Lot lot = getLotOrThrow(lotId);
        Slot changedSlot = lot.changeSlotStatus(levelNumber, slotId, slotStatus);
        return Optional.ofNullable(changedSlot);
    }

    @Override
    public Optional<Slot> removeSlot(String lotId, int levelNumber, int slotId) throws NotFoundException {
        Lot lot = getLotOrThrow(lotId);
        Slot changedSlot = lot.removeSlot(levelNumber, slotId);
        return Optional.ofNullable(changedSlot);
    }

    private Lot getLotOrThrow(String lotId) throws NotFoundException {
        return repo.findById(lotId).orElseThrow(() -> new NotFoundException("Lot not found"));
    }
}
