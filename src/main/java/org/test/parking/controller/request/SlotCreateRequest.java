package org.test.parking.controller.request;

import org.test.parking.domain.space.SlotType;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SlotCreateRequest {
    
    @NotNull
    private SlotType type;
}