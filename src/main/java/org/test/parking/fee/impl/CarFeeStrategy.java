package org.test.parking.fee.impl;

import org.test.parking.fee.FeeStrategy;

import java.math.BigDecimal;

public class CarFeeStrategy implements FeeStrategy {
    
    @Override()
    public BigDecimal calculate(long minutes) {
        return BigDecimal.valueOf((minutes / 60.0) * 2.0);
    }
}
