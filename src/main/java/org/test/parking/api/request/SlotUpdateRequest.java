package org.test.parking.api.request;

import org.test.parking.session.domain.SlotStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SlotUpdateRequest {
    
    @NotNull
    private SlotStatus status;
}
//ASSUMPTION: I do not import lombok.* for best practices, because it is not clear which annotations are used in the class, and it may lead to confusion. So I import only the necessary annotations explicitly. 