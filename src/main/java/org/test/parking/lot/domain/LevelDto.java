package org.test.parking.lot.domain;

import java.util.List;

import lombok.Getter;

@Getter
public class LevelDto {
    private final int number;
    private final String lotId;
    private final List<SlotDto> slots;

    private LevelDto(Level level) {
        this.number = level.getNumber();
        this.lotId = level.getLotId();
        this.slots = level.getSlots();
    }

    public static LevelDto from(Level level) {
        return new LevelDto(level);
    }
}
