package org.test.parking.fee;

import java.math.BigDecimal;

public interface FeeStrategy {
    
    BigDecimal calculate(long minutes);
}
