package org.test.parking.service;

import java.math.BigDecimal;
import org.test.parking.domain.vehicle.Vehicle;

public interface FeeService {
    
    BigDecimal calculate(Vehicle vehicle, long minutes);
}
