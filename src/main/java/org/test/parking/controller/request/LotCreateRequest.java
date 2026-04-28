package org.test.parking.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LotCreateRequest {
    
    @NotBlank
    private String name;
}
