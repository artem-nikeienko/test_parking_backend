package org.test.parking.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.test.parking.domain.session.SlotStatus;
import org.test.parking.domain.space.Level;
import org.test.parking.domain.space.Lot;
import org.test.parking.domain.space.Slot;
import org.test.parking.domain.space.SlotType;
import org.test.parking.exception.ConflictException;
import org.test.parking.exception.domain.LevelNotFoundException;
import org.test.parking.exception.domain.LotNotFoundException;
import org.test.parking.exception.domain.RestrictedSlotOperationException;
import org.test.parking.exception.domain.SlotNotFoundException;
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
            throw new ConflictException(String.format("Lot with name [%s] already exists", lotName));
        }
        Lot lot = new Lot(lotName);
        Lot savedLot = repo.save(lot);
        return savedLot;
    }

    @Override
    public Lot removeLot(String id) throws LotNotFoundException {
        return repo.delete(id)
            .orElseThrow(() -> new LotNotFoundException("Lot not found"));
    }

    @Override
    public Level addLevel(String lotId, int levelNumber)
      throws LotNotFoundException, ConflictException {
        Lot lot = getLotOrThrow(lotId);
        Level addedLevel = lot.addLevel(levelNumber);
        if (addedLevel == null) {
            throw new ConflictException(String.format("Level with number [%d] already exists in lot [%s]", levelNumber, lot.getName()));
        }
        return addedLevel;
    }

    @Override
    public Level removeLevel(String lotId, int levelNumber)
      throws LotNotFoundException, LevelNotFoundException, RestrictedSlotOperationException {
        Lot lot = getLotOrThrow(lotId);
        Level removedLevel = lot.removeLevel(levelNumber);
        if (removedLevel == null) {
            throw new LevelNotFoundException(String.format("Level with number [%d] not found in lot [%s]", levelNumber, lot.getName()));
        }
        return removedLevel;
    }

    @Override
    public Slot addSlot(String lotId, int levelNumber, SlotType slotType)
      throws LotNotFoundException, LevelNotFoundException {
        Lot lot = getLotOrThrow(lotId);
        Slot addedSlot = lot.addSlot(levelNumber, slotType);
        return addedSlot;
    }

    @Override
    public Slot changeSlotStatus(String lotId, int levelNumber, int slotId, SlotStatus slotStatus)
      throws LotNotFoundException, LevelNotFoundException, SlotNotFoundException, RestrictedSlotOperationException {
        Lot lot = getLotOrThrow(lotId);
        Slot changedSlot = lot.changeSlotStatus(levelNumber, slotId, slotStatus);
        return changedSlot;
    }

    @Override
    public Slot removeSlot(String lotId, int levelNumber, int slotId)
      throws LotNotFoundException, LevelNotFoundException, SlotNotFoundException, RestrictedSlotOperationException {
        Lot lot = getLotOrThrow(lotId);
        Slot changedSlot = lot.removeSlot(levelNumber, slotId);
        return changedSlot;
    }

    private Lot getLotOrThrow(String lotId) throws LotNotFoundException {
        return repo.findById(lotId).orElseThrow(() -> new LotNotFoundException("Lot not found"));
    }
}
