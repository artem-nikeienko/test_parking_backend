package org.test.parking.session.domain;

import org.test.parking.lot.domain.SlotType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SlotAssignment {
    private final String lotId;
    private final Integer levelNumber;
    private final Integer slotId;
    private final SlotType slotType;
}
