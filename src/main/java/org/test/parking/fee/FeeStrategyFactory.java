package org.test.parking.fee;

import org.test.parking.domain.vehicle.VehicleType;
import org.test.parking.fee.impl.*;

import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class FeeStrategyFactory {

    private final Map<VehicleType, FeeStrategy> strategies = new EnumMap<>(VehicleType.class);

    public FeeStrategyFactory() {
        strategies.put(VehicleType.CAR, new CarFeeStrategy());
        strategies.put(VehicleType.MOTORCYCLE, new MotorcycleFeeStrategy());
        strategies.put(VehicleType.TRUCK, new TruckFeeStrategy());
    }

    public FeeStrategy get(VehicleType type) {
        return strategies.get(type);
    }
}
