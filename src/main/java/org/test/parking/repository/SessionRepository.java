package org.test.parking.repository;

import java.util.*;
import org.test.parking.domain.session.ParkingSession;

public interface SessionRepository {
    ParkingSession save(ParkingSession session);
    Optional<ParkingSession> findById(String id);
    Optional<ParkingSession> findActiveByPlate(String plate);
    List<ParkingSession> findAllActive();
}
