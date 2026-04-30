package org.test.parking.api.request;

import org.test.parking.vehicle.domain.Vehicle;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckInRequest {
    
    @NotNull @Valid
    private Vehicle vehicle;
}