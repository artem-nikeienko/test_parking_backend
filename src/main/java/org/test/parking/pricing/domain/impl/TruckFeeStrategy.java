package org.test.parking.pricing.domain.impl;

import java.math.BigDecimal;

import org.test.parking.pricing.domain.FeeStrategy;

public class TruckFeeStrategy implements FeeStrategy {
    
    @Override()
    public BigDecimal calculate(long minutes) {
        return BigDecimal.valueOf((minutes / 60.0) * 3.0);
    }
}
