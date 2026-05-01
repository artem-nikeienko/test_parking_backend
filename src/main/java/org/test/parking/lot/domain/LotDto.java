package org.test.parking.lot.domain;

import java.util.List;

import lombok.Getter;

@Getter
public class LotDto {
    private final String id;
    private final String name;
    private final List<LevelDto> levels;

    private LotDto(Lot lot) {
        this.id = lot.getId();
        this.name = lot.getName();
        this.levels = lot.getLevels();
    }

    public static LotDto from(Lot lot) {
        return new LotDto(lot);
    }
}
