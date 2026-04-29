package org.test.parking.service;

import org.test.parking.domain.model.session.SlotStatus;
import org.test.parking.domain.model.space.Level;
import org.test.parking.domain.model.space.Lot;
import org.test.parking.domain.model.space.Slot;
import org.test.parking.domain.model.space.SlotType;
import org.test.parking.exception.ConflictException;
import org.test.parking.exception.domain.LevelNotFoundException;
import org.test.parking.exception.domain.LotNotFoundException;
import org.test.parking.exception.domain.RestrictedSlotOperationException;
import org.test.parking.exception.domain.SlotNotFoundException;

public interface ParkingSpaceService {

    /**
     * Creates a new parking lot.
     *
     * @param lotName Unique name of the parking lot.
     * @return Created {@link Lot}.
     * @throws ConflictException if a lot with the same name already exists.
     */
    Lot addLot(String lotName) throws ConflictException;

    /**
     * Removes a parking lot by its identifier.
     *
     * @param id Lot identifier.
     * @return Removed {@link Lot}.
     * @throws LotNotFoundException if the lot does not exist.
     */
    Lot removeLot(String id) throws LotNotFoundException;

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
     * @throws RestrictedSlotOperationException if the level contains occupied slots and cannot be removed.
     */
    void removeLevel(String lotId, int levelNumber) throws LotNotFoundException, LevelNotFoundException, RestrictedSlotOperationException;

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
     * @throws RestrictedSlotOperationException if the status change is invalid (e.g., trying to set to UNAVAILABLE while occupied).
     */
    void changeSlotStatus(String lotId, int levelNumber, int slotId, SlotStatus slotStatus)
        throws LotNotFoundException, LevelNotFoundException, SlotNotFoundException, RestrictedSlotOperationException;

    /**
     * Removes a parking slot from a level.
     *
     * @param lotId       Identifier of the lot.
     * @param levelNumber Level number.
     * @param slotId      Slot identifier.
     * @throws LotNotFoundException if the lot does not exist.
     * @throws LevelNotFoundException if the level does not exist.
     * @throws RestrictedSlotOperationException if the slot is currently occupied and cannot be removed.
     */
    void removeSlot(String lotId, int levelNumber, int slotId) throws LotNotFoundException, LevelNotFoundException, SlotNotFoundException, RestrictedSlotOperationException;
}