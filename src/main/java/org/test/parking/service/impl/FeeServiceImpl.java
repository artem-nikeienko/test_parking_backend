package org.test.parking.service.impl;

import org.test.parking.domain.vehicle.Vehicle;
import org.test.parking.fee.FeeStrategyFactory;
import org.test.parking.service.FeeService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class FeeServiceImpl implements FeeService {

    private final FeeStrategyFactory factory;

    public FeeServiceImpl(FeeStrategyFactory factory) {
        this.factory = factory;
    }

    @Override
    public BigDecimal calculate(Vehicle vehicle, long minutes) {
        return factory.get(vehicle.getType()).calculate(minutes);
    }
}
