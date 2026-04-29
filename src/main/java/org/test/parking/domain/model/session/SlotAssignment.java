package org.test.parking.domain.model.session;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SlotAssignment {
    private final int levelNumber;
    private final int slotId;
}
