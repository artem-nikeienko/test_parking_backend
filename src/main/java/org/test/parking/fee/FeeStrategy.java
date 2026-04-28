package org.test.parking.fee;

import java.math.BigDecimal;

public interface FeeStrategy {
    
    /**
     * Calculates parking fee based on the duration expressed in minutes.
     *
     * @param minutes Total parking duration in minutes. Must be non-negative.
     * @return Calculated fee as {@link BigDecimal}. Never null.
     *
     * @throws IllegalArgumentException if minutes is negative.
     *
     * Note:
     * - Implementations may apply rounding rules (e.g., ceil to full hour).
     * - Different strategies may represent different pricing models (flat rate, hourly, progressive, etc.).
     */
    BigDecimal calculate(long minutes);
}
