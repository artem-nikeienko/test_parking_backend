package org.test.parking.lot.service;

import org.test.parking.exception.domain.ConflictException;
import org.test.parking.exception.domain.LevelNotFoundException;
import org.test.parking.exception.domain.LotNotFoundException;
import org.test.parking.exception.domain.RestrictedLotOperationException;
import org.test.parking.exception.domain.SlotNotFoundException;
import org.test.parking.lot.domain.Level;
import org.test.parking.lot.domain.LevelDto;
import org.test.parking.lot.domain.Lot;
import org.test.parking.lot.domain.LotDto;
import org.test.parking.lot.domain.Slot;
import org.test.parking.lot.domain.SlotDto;
import org.test.parking.lot.domain.SlotType;
import org.test.parking.session.domain.SlotStatus;

/**
 * Provides operations for managing parking space structure.
 * <p>
 * Includes creation and removal of parking lots, levels, and slots,
 * as well as management of slot status.
 * </p>
 */
public interface ParkingLotService {

    /**
     * Creates a new parking lot.
     *
     * @param lotName Unique name of the parking lot.
     * @return {@link LotDto} representing created {@link Lot}.
     * @throws ConflictException if a lot with the same name already exists.
     */
    LotDto addLot(String lotName) throws ConflictException;

    /**
     * Removes a parking lot by its identifier.
     *
     * @param id Lot identifier.
     * @return {@link LotDto} representing deleted {@link Lot}.
     * @throws LotNotFoundException if the lot does not exist.
     * @throws RestrictedLotOperationException if the lot contains occupied slots and cannot be removed.
     */
    LotDto removeLot(String id) throws LotNotFoundException, RestrictedLotOperationException;

    /**
     * Adds a new level to a parking lot.
     *
     * @param lotId       Identifier of the lot.
     * @param levelNumber Level number (must be unique within the lot).
     * @return {@link LevelDto} representing created {@link Level}.
     * @throws LotNotFoundException if the lot does not exist.
     * @throws ConflictException if a level with the same number already exists in the lot.
     */
    LevelDto addLevel(String lotId, int levelNumber) throws LotNotFoundException, ConflictException;

    /**
     * Removes a level from a parking lot.
     *
     * @param lotId       Identifier of the lot.
     * @param levelNumber Level number.
     * @return {@link LevelDto} representing removed {@link Level}.
     * @throws LotNotFoundException if the lot does not exist.
     * @throws LevelNotFoundException if the level does not exist.
     * @throws RestrictedLotOperationException if the level contains occupied slots and cannot be removed.
     */
    LevelDto removeLevel(String lotId, int levelNumber) throws LotNotFoundException, LevelNotFoundException, RestrictedLotOperationException;

    /**
     * Adds a new parking slot to a specific level.
     *
     * @param lotId       Identifier of the lot.
     * @param levelNumber Level number.
     * @param slotType    Type of the slot.
     * @return {@link SlotDto} representing created {@link Slot}.
     * @throws LotNotFoundException if the lot does not exist.
     * @throws LevelNotFoundException if the level does not exist.
     */
    SlotDto addSlot(String lotId, int levelNumber, SlotType slotType) throws LotNotFoundException, LevelNotFoundException;

    /**
     * Changes the status of a parking slot (e.g., AVAILABLE, UNAVAILABLE).
     *
     * @param lotId       Identifier of the lot.
     * @param levelNumber Level number.
     * @param slotId      Slot identifier.
     * @param slotStatus  New status.
     * @return {@link SlotDto} representing updated {@link Slot}.
     * @throws LotNotFoundException if the lot does not exist.
     * @throws LevelNotFoundException if the level does not exist.
     * @throws SlotNotFoundException if the slot does not exist.
     * @throws RestrictedLotOperationException if the status change is invalid (e.g., trying to set to UNAVAILABLE while occupied).
     */
    SlotDto changeSlotStatus(String lotId, int levelNumber, int slotId, SlotStatus slotStatus)
        throws LotNotFoundException, LevelNotFoundException, SlotNotFoundException, RestrictedLotOperationException;

    /**
     * Removes a parking slot from a level.
     *
     * @param lotId       Identifier of the lot.
     * @param levelNumber Level number.
     * @param slotId      Slot identifier.
     * @return {@link SlotDto} representing removed {@link Slot}.
     * @throws LotNotFoundException if the lot does not exist.
     * @throws LevelNotFoundException if the level does not exist.
     * @throws RestrictedLotOperationException if the slot is currently occupied and cannot be removed.
     */
    SlotDto removeSlot(String lotId, int levelNumber, int slotId) throws LotNotFoundException, LevelNotFoundException, SlotNotFoundException, RestrictedLotOperationException;

}