package org.test.parking.domain.payment;

import java.time.Duration;

public interface FeeStrategy {
    double calculateFee(Duration duration);
}
