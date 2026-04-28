package org.test.parking.domain.payment;

import java.time.Duration;

public class HourlyFeeStrategy implements FeeStrategy {

    private final double ratePerHour;

    public HourlyFeeStrategy(double ratePerHour) {
        this.ratePerHour = ratePerHour;
    }

    @Override
    public double calculateFee(Duration duration) {
        long hours = Math.max(1, duration.toHours());
        return hours * ratePerHour;
    }

}
