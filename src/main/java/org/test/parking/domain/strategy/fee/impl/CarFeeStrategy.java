package org.test.parking.domain.strategy.fee.impl;

import java.math.BigDecimal;

import org.test.parking.domain.strategy.fee.FeeStrategy;

public class CarFeeStrategy implements FeeStrategy {
    
    @Override()
    public BigDecimal calculate(long minutes) {
        return BigDecimal.valueOf((minutes / 60.0) * 2.0);
    }
}
