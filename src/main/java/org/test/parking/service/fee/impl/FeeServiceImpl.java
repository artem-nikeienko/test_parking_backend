package org.test.parking.service.fee.impl;

import org.test.parking.domain.model.vehicle.Vehicle;
import org.test.parking.domain.strategy.fee.FeeStrategyFactory;
import org.test.parking.service.fee.FeeService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

import org.test.parking.domain.model.session.ParkingSession;

@Service
public class FeeServiceImpl implements FeeService {

    private final FeeStrategyFactory factory;

    public FeeServiceImpl(FeeStrategyFactory factory) {
        this.factory = factory;
    }

    @Override
    public BigDecimal calculate(ParkingSession session) {
        long minutes = Duration.between(session.getEntryTime(), session.getExitTime()).toMinutes();
        Vehicle vehicle = session.getVehicle();
        return factory.get(vehicle.getType())
            .calculate(minutes)
            .setScale(2, RoundingMode.HALF_UP);
    }
}
