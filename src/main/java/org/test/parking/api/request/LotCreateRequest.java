package org.test.parking.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LotCreateRequest {
    
    @NotBlank
    private String name;
}
