package org.test.parking.repository;

import java.util.List;
import java.util.Optional;

import org.test.parking.domain.model.session.ParkingSession;

/**
 * Repository abstraction for managing parking session persistence.
 * <p>
 * Responsible for storing and retrieving parking sessions,
 * including active and completed sessions.
 * </p>
 */
public interface SessionRepository {

    /**
     * Persists a parking session.
     *
     * @param session Session to save.
     * @return Saved session instance.
     *
     */
    ParkingSession save(ParkingSession session);

    /**
     * Finds a session by its identifier.
     *
     * @param id Session identifier.
     * @return Optional containing session if found.
     */
    Optional<ParkingSession> findById(String id);

    /**
     * Finds an active session by vehicle license plate.
     *
     * @param plate Vehicle license plate.
     * @return Optional containing active session if exists.
     *
     * Notes:
     * - At most one active session per vehicle is expected.
     */
    Optional<ParkingSession> findActiveByPlate(String plate);

    /**
     * Retrieves all active sessions.
     *
     * @param lotId Identifier of the parking lot sessions meant to be get for.
     * 
     * @return List of active sessions.
     */
    List<ParkingSession> findAllActive(String lotId);
}
