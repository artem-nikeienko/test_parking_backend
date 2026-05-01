package org.test.parking.lot.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.test.parking.exception.domain.ConflictException;
import org.test.parking.exception.domain.LevelNotFoundException;
import org.test.parking.exception.domain.LotNotFoundException;
import org.test.parking.exception.domain.RestrictedLotOperationException;
import org.test.parking.exception.domain.SlotNotFoundException;
import org.test.parking.lot.domain.LevelDto;
import org.test.parking.lot.domain.Lot;
import org.test.parking.lot.domain.LotDto;
import org.test.parking.lot.domain.SlotDto;
import org.test.parking.lot.domain.SlotType;
import org.test.parking.lot.repository.LotRepository;
import org.test.parking.lot.service.ParkingLotService;
import org.test.parking.session.domain.SlotStatus;

@Service
public class ParkingLotServiceImpl implements ParkingLotService {

    private final LotRepository lotRepo;

    public ParkingLotServiceImpl(LotRepository repo) {
        this.lotRepo = repo;
    }

    @Override
    public LotDto addLot(String lotName) throws ConflictException {
      Optional<Lot> optLot = lotRepo.findByName(lotName);
      if (optLot.isPresent()) {
          throw new ConflictException(String.format("Lot with name [%s] already exists", lotName));
      }
      Lot lot = new Lot(lotName);
      Lot savedLot = lotRepo.save(lot);
      return LotDto.from(savedLot);
    }

    @Override
    public LotDto removeLot(String id) throws LotNotFoundException, RestrictedLotOperationException {
      Lot lot = getLotOrThrow(id);
      if (lot.hasOccupiedSlots()) {
        throw new RestrictedLotOperationException(String.format("Lot with id [%s] in level has occupied slot(s) and cannot be removed", id));
      }
      lotRepo.delete(id);
      return LotDto.from(lot);
    }

    @Override
    public LevelDto addLevel(String lotId, int levelNumber)
      throws LotNotFoundException, ConflictException {
        Lot lot = getLotOrThrow(lotId);
        LevelDto addedLevel = lot.addLevel(levelNumber);
        return addedLevel;
    }

    @Override
    public LevelDto removeLevel(String lotId, int levelNumber)
      throws LotNotFoundException, LevelNotFoundException, RestrictedLotOperationException {
        Lot lot = getLotOrThrow(lotId);
        LevelDto removedLevel = lot.removeLevel(levelNumber);
        return removedLevel;
    }

    @Override
    public SlotDto addSlot(String lotId, int levelNumber, SlotType slotType)
      throws LotNotFoundException, LevelNotFoundException {
        Lot lot = getLotOrThrow(lotId);
        SlotDto addedSlot = lot.addSlot(levelNumber, slotType);
        return addedSlot;
    }

    @Override
    public SlotDto changeSlotStatus(String lotId, int levelNumber, int slotId, SlotStatus slotStatus)
      throws LotNotFoundException, LevelNotFoundException, SlotNotFoundException, RestrictedLotOperationException {
        Lot lot = getLotOrThrow(lotId);
        SlotDto updatedSlot = lot.changeSlotStatus(levelNumber, slotId, slotStatus);
        return updatedSlot;
    }

    @Override
    public SlotDto removeSlot(String lotId, int levelNumber, int slotId)
      throws LotNotFoundException, LevelNotFoundException, SlotNotFoundException, RestrictedLotOperationException {
        Lot lot = getLotOrThrow(lotId);
        SlotDto removedSlot = lot.removeSlot(levelNumber, slotId);
        return removedSlot;
    }

    private Lot getLotOrThrow(String lotId) throws LotNotFoundException {
        return lotRepo.findById(lotId).orElseThrow(() -> new LotNotFoundException(lotId));
    }
}
