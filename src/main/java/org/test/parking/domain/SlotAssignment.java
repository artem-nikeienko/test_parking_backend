package org.test.parking.domain;

import org.test.parking.domain.space.Level;
import org.test.parking.domain.space.Slot;

public class SlotAssignment {

    private final Level level;
    private final Slot slot;

    public SlotAssignment(Level level, Slot slot) {
        this.level = level;
        this.slot = slot;
    }

    public Level getLevel() {
        return level;
    }

    public Slot getSlot() {
        return slot;
    }

}
