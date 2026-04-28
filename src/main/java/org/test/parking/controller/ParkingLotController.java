package org.test.parking.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.test.parking.controller.request.LevelCreateRequest;
import org.test.parking.controller.request.LotCreateRequest;
import org.test.parking.controller.request.SlotCreateRequest;
import org.test.parking.controller.request.SlotUpdateRequest;
import org.test.parking.domain.space.Level;
import org.test.parking.domain.space.Lot;
import org.test.parking.domain.space.Slot;
import org.test.parking.exception.ConflictException;
import org.test.parking.service.ParkingSpaceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/parking")
public class ParkingLotController {

    private final ParkingSpaceService spaceService;

    public ParkingLotController(ParkingSpaceService spaceService) {
        this.spaceService = spaceService;
    }

    @PostMapping("/lots")
    public Lot createLot(@Valid @RequestBody LotCreateRequest request) {
        //ASSUMPTION: Request object stays at the Controller layer, Service layer gets only its attributes needed for business logic to avoid excessive coupling between layers.
        try {
            return spaceService.addLot(request.getName());
        } catch (Exception e) {
            return null;
        }
    }

    @DeleteMapping("/lots/{lotId}")
    public void deleteLot(@PathVariable String lotId) {
        spaceService.removeLot(lotId);
    }

    @PostMapping("/lots/{lotId}/levels")
    public Level createLevel(@PathVariable String lotId, @Valid @RequestBody LevelCreateRequest request) throws Exception {
        //TODO: Explicitly tell about Lombok annotations processing in documentation (README.md) to avoid confusion for developers who are not familiar with Lombok, because it may lead to confusion about where the getters and setters are coming from, etc.
        return spaceService.addLevel(lotId, request.getNumber())
            .orElseThrow(() -> new ConflictException(lotId + " already has level " + request.getNumber()));
    }

    @DeleteMapping("/lots/{lotId}/levels/{levelNumber}")
    public void deleteLevel(@PathVariable String lotId, @PathVariable Integer levelNumber) {
        try {
            spaceService.removeLevel(lotId, levelNumber);
        } catch (Exception e) {
            
        }
    }

    @PostMapping("/lots/{lotId}/levels/{levelNumber}/slots")
    //TODO: Use some Response object instead of returning Slot directly, because we may want to return additional info in the future, such as the URL of the created slot, etc.
    //TODO: align openapi.yaml with controllers
    public Slot createSlot(@PathVariable String lotId, @PathVariable Integer levelNumber, @Valid @RequestBody SlotCreateRequest request) {
        try {
            return spaceService.addSlot(lotId, levelNumber, request.getType());
        } catch (Exception e) {
            return null;
        }
    }

    @PatchMapping("/lots/{lotId}/levels/{levelNumber}/slots/{slotId}")
    public Slot updateSlot(@PathVariable String lotId, @PathVariable Integer levelNumber, @PathVariable int slotId, @Valid @RequestBody SlotUpdateRequest request) throws Exception {
        return spaceService.changeSlotStatus(lotId, levelNumber, slotId, request.getStatus())
            .orElseThrow(() -> new ConflictException("Failed to change status of slot " + slotId + " on level " + levelNumber + " in lot " + lotId));
    }

    @DeleteMapping("/lots/{lotId}/levels/{levelNumber}/slots/{slotId}")
    public void deleteSlot(@PathVariable String lotId, @PathVariable Integer levelNumber, @PathVariable int slotId) {
        try {
            spaceService.removeSlot(lotId, levelNumber, slotId);
        } catch (Exception e) {
        }
    }
}