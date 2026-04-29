package org.test.parking.domain.model.session;

import org.test.parking.domain.model.space.SlotType;

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
