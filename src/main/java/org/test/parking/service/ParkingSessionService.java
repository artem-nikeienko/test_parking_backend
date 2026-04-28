package org.test.parking.service;

import java.util.List;

import org.test.parking.controller.response.CheckOutResponse;
import org.test.parking.domain.session.ParkingSession;
import org.test.parking.domain.vehicle.Vehicle;
import org.test.parking.exception.ConflictException;
import org.test.parking.exception.NotFoundException;

public interface ParkingSessionService {
    
    //TODO: add appropriate Exceptions of BusinessException type, e.g. LotFullException, VehicleAlreadyParkedException, etc.
    ParkingSession checkIn(String lotId, Vehicle vehicle) throws ConflictException, NotFoundException;
    
    //TODO: replace Controller level Response with domain level parameters
    CheckOutResponse checkOut(String sessionId) throws NotFoundException;
    
    //TODO: document with comments service interfaces
    List<ParkingSession> getActiveSessions();
}
