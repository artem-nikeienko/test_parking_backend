package org.test.parking.controller.request;

import org.test.parking.domain.model.vehicle.Vehicle;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckInRequest {
    
    @NotNull @Valid
    private Vehicle vehicle;
}