package org.test.parking.pricing.service.impl;

import org.test.parking.pricing.domain.FeeStrategyFactory;
import org.test.parking.pricing.service.FeeService;
import org.test.parking.session.domain.ParkingSession;
import org.test.parking.vehicle.domain.Vehicle;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

import org.test.parking.lot.domain.Level;

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
