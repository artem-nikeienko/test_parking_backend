package org.test.parking.api.request;

import org.test.parking.lot.domain.SlotType;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SlotCreateRequest {
    
    @NotNull
    private SlotType type;
}