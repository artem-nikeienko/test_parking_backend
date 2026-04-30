package org.test.parking.pricing.service;

import java.math.BigDecimal;

import org.test.parking.session.domain.ParkingSession;

public interface FeeService {
    
    BigDecimal calculate(ParkingSession session);
}
