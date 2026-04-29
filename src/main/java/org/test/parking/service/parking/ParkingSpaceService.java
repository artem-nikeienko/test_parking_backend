package org.test.parking.service.parking;

import org.test.parking.domain.model.session.SlotStatus;
import org.test.parking.domain.model.space.Level;
import org.test.parking.domain.model.space.Lot;
import org.test.parking.domain.model.space.Slot;
import org.test.parking.domain.model.space.SlotType;
import org.test.parking.exception.ConflictException;
import org.test.parking.exception.domain.LevelNotFoundException;
import org.test.parking.exception.domain.LotNotFoundException;
import org.test.parking.exception.domain.RestrictedLotOperationException;
import org.test.parking.exception.domain.SlotNotFoundException;

/**
 * Provides operations for managing parking space structure.
 * <p>
 * Includes creation and removal of parking lots, levels, and slots,
 * as well as management of slot status.
 * </p>
 */
public interface ParkingSpaceService {

    /**
     * Creates a new parking lot.
     *
     * @param lotName Unique name of the parking lot.
     * @return Created {@link Lot} ID.
     * @throws ConflictException if a lot with the same name already exists.
     */
    String addLot(String lotName) throws ConflictException;

    /**
     * Removes a parking lot by its identifier.
     *
     * @param id Lot identifier.
     * @return Removed {@link Lot} ID.
     * @throws LotNotFoundException if the lot does not exist.
     * @throws RestrictedLotOperationException if the lot contains occupied slots and cannot be removed.
     */
    String removeLot(String id) throws LotNotFoundException, RestrictedLotOperationException;

    /**
     * Adds a new level to a parking lot.
     *
     * @param lotId       Identifier of the lot.
     * @param levelNumber Level number (must be unique within the lot).
     * @return Created {@link Level} number.
     * @throws LotNotFoundException if the lot does not exist.
     * @throws ConflictException if a level with the same number already exists in the lot.
     */
    int addLevel(String lotId, int levelNumber) throws LotNotFoundException, ConflictException;

    /**
     * Removes a level from a parking lot.
     *
     * @param lotId       Identifier of the lot.
     * @param levelNumber Level number.
     * @return Removed {@link Link}.
     * @throws LotNotFoundException if the lot does not exist.
     * @throws LevelNotFoundException if the level does not exist.
     * @throws RestrictedLotOperationException if the level contains occupied slots and cannot be removed.
     */
    void removeLevel(String lotId, int levelNumber) throws LotNotFoundException, LevelNotFoundException, RestrictedLotOperationException;

    /**
     * Adds a new parking slot to a specific level.
     *
     * @param lotId       Identifier of the lot.
     * @param levelNumber Level number.
     * @param slotType    Type of the slot.
     * @return Created {@link Slot} id.
     * @throws LotNotFoundException if the lot does not exist.
     * @throws LevelNotFoundException if the level does not exist.
     */
    int addSlot(String lotId, int levelNumber, SlotType slotType) throws LotNotFoundException, LevelNotFoundException;

    /**
     * Changes the status of a parking slot (e.g., AVAILABLE, UNAVAILABLE).
     *
     * @param lotId       Identifier of the lot.
     * @param levelNumber Level number.
     * @param slotId      Slot identifier.
     * @param slotStatus  New status.
     * @throws LotNotFoundException if the lot does not exist.
     * @throws LevelNotFoundException if the level does not exist.
     * @throws SlotNotFoundException if the slot does not exist.
     * @throws RestrictedLotOperationException if the status change is invalid (e.g., trying to set to UNAVAILABLE while occupied).
     */
    void changeSlotStatus(String lotId, int levelNumber, int slotId, SlotStatus slotStatus)
        throws LotNotFoundException, LevelNotFoundException, SlotNotFoundException, RestrictedLotOperationException;

    /**
     * Removes a parking slot from a level.
     *
     * @param lotId       Identifier of the lot.
     * @param levelNumber Level number.
     * @param slotId      Slot identifier.
     * @throws LotNotFoundException if the lot does not exist.
     * @throws LevelNotFoundException if the level does not exist.
     * @throws RestrictedLotOperationException if the slot is currently occupied and cannot be removed.
     */
    void removeSlot(String lotId, int levelNumber, int slotId) throws LotNotFoundException, LevelNotFoundException, SlotNotFoundException, RestrictedLotOperationException;
}