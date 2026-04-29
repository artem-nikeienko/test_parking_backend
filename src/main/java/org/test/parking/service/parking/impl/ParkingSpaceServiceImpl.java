package org.test.parking.service.parking.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.test.parking.domain.model.session.SlotStatus;
import org.test.parking.domain.model.space.Lot;
import org.test.parking.domain.model.space.SlotType;
import org.test.parking.exception.ConflictException;
import org.test.parking.exception.domain.LevelNotFoundException;
import org.test.parking.exception.domain.LotNotFoundException;
import org.test.parking.exception.domain.RestrictedLotOperationException;
import org.test.parking.exception.domain.SlotNotFoundException;
import org.test.parking.repository.LotRepository;
import org.test.parking.service.parking.ParkingSpaceService;

@Service
public class ParkingSpaceServiceImpl implements ParkingSpaceService {

    private final LotRepository lotRepo;

    public ParkingSpaceServiceImpl(LotRepository repo) {
        this.lotRepo = repo;
    }

    @Override
    public String addLot(String lotName) throws ConflictException {
      Optional<Lot> optLot = lotRepo.findByName(lotName);
      if (optLot.isPresent()) {
          throw new ConflictException(String.format("Lot with name [%s] already exists", lotName));
      }
      Lot lot = new Lot(lotName);
      Lot savedLot = lotRepo.save(lot);
      return savedLot.getId();
    }

    @Override
    public String removeLot(String id) throws LotNotFoundException, RestrictedLotOperationException {
      Lot lot = getLotOrThrow(id);
      if (lot.hasOccupiedSlots()) {
        throw new RestrictedLotOperationException(String.format("Lot with id [%s] in level has occupied slot(s) and cannot be removed", id));
      }
      lotRepo.delete(id);
      return id;
    }

    @Override
    public int addLevel(String lotId, int levelNumber)
      throws LotNotFoundException, ConflictException {
        Lot lot = getLotOrThrow(lotId);
        int addedLevelNumber = lot.addLevel(levelNumber);
        return addedLevelNumber;
    }

    @Override
    public void removeLevel(String lotId, int levelNumber)
      throws LotNotFoundException, LevelNotFoundException, RestrictedLotOperationException {
        Lot lot = getLotOrThrow(lotId);
        lot.removeLevel(levelNumber);
    }

    @Override
    public int addSlot(String lotId, int levelNumber, SlotType slotType)
      throws LotNotFoundException, LevelNotFoundException {
        Lot lot = getLotOrThrow(lotId);
        int addedSlotId = lot.addSlot(levelNumber, slotType);
        return addedSlotId;
    }

    @Override
    public void changeSlotStatus(String lotId, int levelNumber, int slotId, SlotStatus slotStatus)
      throws LotNotFoundException, LevelNotFoundException, SlotNotFoundException, RestrictedLotOperationException {
        Lot lot = getLotOrThrow(lotId);
        lot.changeSlotStatus(levelNumber, slotId, slotStatus);
    }

    @Override
    public void removeSlot(String lotId, int levelNumber, int slotId)
      throws LotNotFoundException, LevelNotFoundException, SlotNotFoundException, RestrictedLotOperationException {
        Lot lot = getLotOrThrow(lotId);
        lot.removeSlot(levelNumber, slotId);
    }

    private Lot getLotOrThrow(String lotId) throws LotNotFoundException {
        return lotRepo.findById(lotId).orElseThrow(() -> new LotNotFoundException(lotId));
    }
}
