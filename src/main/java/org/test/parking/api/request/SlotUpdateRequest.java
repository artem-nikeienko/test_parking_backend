package org.test.parking.api.request;

import org.test.parking.session.domain.SlotStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SlotUpdateRequest {
    
    @NotNull
    private SlotStatus status;
}