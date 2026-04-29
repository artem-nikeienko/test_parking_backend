package org.test.parking.service.fee;

import java.math.BigDecimal;

import org.test.parking.domain.model.session.ParkingSession;

public interface FeeService {
    
    BigDecimal calculate(ParkingSession session);
}
