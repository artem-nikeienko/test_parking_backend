package org.test.parking.service.session;

import java.util.List;

import org.test.parking.domain.model.session.ParkingSession;
import org.test.parking.domain.model.vehicle.Vehicle;
import org.test.parking.exception.domain.LotNotFoundException;
import org.test.parking.exception.domain.NoAvailableSlotsException;
import org.test.parking.exception.domain.RestrictedLotOperationException;
import org.test.parking.exception.domain.SessionAlreadyCompletedException;
import org.test.parking.exception.domain.SessionNotFoundException;
import org.test.parking.exception.domain.VehicleParkedException;

/**
 * Provides operations for managing parking sessions lifecycle.
 * <p>
 * Includes vehicle check-in, check-out, and retrieval of active sessions.
 * </p>
 */
public interface ParkingSessionService {
    
    /**
     * Checks in a vehicle into a parking lot.
     *
     * @param lotId   Identifier of the parking lot.
     * @param vehicle Vehicle to be parked.
     * @return Created {@link ParkingSession}.
     *
     * @throws LotNotFoundException if the lot does not exist.
     * @throws VehicleParkedException if the vehicle is already parked.
     * @throws NoAvailableSlotsException if no slot matches vehicle type.
     * @throws RestrictedLotOperationException if the allocated slot is under maintenance or has restrictions preventing parking.
     */
    ParkingSession checkIn(String lotId, Vehicle vehicle)
        throws VehicleParkedException, LotNotFoundException, NoAvailableSlotsException, RestrictedLotOperationException;

    /**
     * Checks out a vehicle by session ID.
     *
     * @param sessionId Identifier of the parking session.
     * @return {@link ParkingSession} containing fee and timing details. Idempotent operation - multiple calls with same sessionId should return the same result after first checkout read from repository.
     *
     * @throws SessionNotFoundException if the session does not exist.
     * @throws SessionAlreadyCompletedException if the session is already completed.
     */
    //TODO: replace Controller level Response with domain level parameters
    ParkingSession checkOut(String sessionId)
        throws SessionNotFoundException, SessionAlreadyCompletedException;

    /**
     * Retrieves all active parking sessions.
     *
     * @param lotId Identifier of the parking lot sessions meant to be get for.
     * 
     * @return List of active {@link ParkingSession}. Never null.
     *
     * Notes:
     * - Active sessions are those without checkout timestamp.
     */
    List<ParkingSession> getActiveSessions(String lotId);
}
