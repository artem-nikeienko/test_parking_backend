package org.test.parking.service;

import java.util.List;

import org.test.parking.controller.response.CheckOutResponse;
import org.test.parking.domain.session.ParkingSession;
import org.test.parking.domain.vehicle.Vehicle;
import org.test.parking.exception.IncompatibleVehicleException;
import org.test.parking.exception.LotFullException;
import org.test.parking.exception.NotFoundException;
import org.test.parking.exception.VehicleParkedException;
import org.test.parking.exception.domain.LotNotFoundException;
import org.test.parking.exception.domain.RestrictedSlotOperationException;
import org.test.parking.exception.domain.SessionNotFoundException;

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
     * @throws LotFullException if no suitable slots are available.
     * @throws IncompatibleVehicleException if no slot matches vehicle type.
     * @throws RestrictedSlotOperationException if the allocated slot is under maintenance or has restrictions preventing parking.
     */
    ParkingSession checkIn(String lotId, Vehicle vehicle)
        throws VehicleParkedException, LotNotFoundException, LotFullException, IncompatibleVehicleException, RestrictedSlotOperationException;

    /**
     * Checks out a vehicle by session ID.
     *
     * @param sessionId Identifier of the parking session.
     * @return {@link CheckOutResponse} containing fee and timing details. Idempotent operation - multiple calls with same sessionId should return the same result after first checkout read from repository.
     *
     * @throws SessionNotFoundException if the session does not exist.
     */
    //TODO: replace Controller level Response with domain level parameters
    CheckOutResponse checkOut(String sessionId)
        throws SessionNotFoundException;

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
