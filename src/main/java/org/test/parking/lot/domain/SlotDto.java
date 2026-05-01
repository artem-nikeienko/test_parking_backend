package org.test.parking.lot.domain;

import org.test.parking.session.domain.SlotStatus;

import lombok.Getter;

@Getter
public class SlotDto {
    private final int id;
    private final int levelNumber; 
    private final SlotType type;
    private final SlotStatus status;

    private SlotDto(Slot slot) {
        this.id = slot.getId();
        this.levelNumber = slot.getLevelNumber();
        this.type = slot.getType();
        this.status = slot.getStatus();
    }

    public static SlotDto from(Slot slot) {
        return new SlotDto(slot);
    }
}
