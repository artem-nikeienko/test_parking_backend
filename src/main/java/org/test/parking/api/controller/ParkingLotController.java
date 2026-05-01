package org.test.parking.api.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.test.parking.api.request.LevelCreateRequest;
import org.test.parking.api.request.LotCreateRequest;
import org.test.parking.api.request.SlotCreateRequest;
import org.test.parking.api.request.SlotUpdateRequest;
import org.test.parking.exception.NotFoundException;
import org.test.parking.exception.domain.ConflictException;
import org.test.parking.exception.domain.DomainException;
import org.test.parking.exception.domain.LevelNotFoundException;
import org.test.parking.exception.domain.LotNotFoundException;
import org.test.parking.exception.domain.SlotNotFoundException;
import org.test.parking.lot.domain.LevelDto;
import org.test.parking.lot.domain.LotDto;
import org.test.parking.lot.domain.SlotDto;
import org.test.parking.lot.service.ParkingLotService;

import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/api/v1/parking")
public class ParkingLotController {

    private final ParkingLotService spaceService;

    public ParkingLotController(ParkingLotService spaceService) {
        this.spaceService = spaceService;
    }

    @PostMapping("/lots")
    public ResponseEntity<LotDto> createLot(@Valid @RequestBody LotCreateRequest request) throws ConflictException {
        LotDto createdLot = spaceService.addLot(request.getName());
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{lotId}")
            .buildAndExpand(createdLot.getId())
            .toUri();
        return ResponseEntity.created(location).body(createdLot);
    }

    @DeleteMapping("/lots/{lotId}")
    public ResponseEntity<Void> deleteLot(@PathVariable String lotId) throws NotFoundException, DomainException {
        try {
            LotDto removedLot = spaceService.removeLot(lotId);
            return ResponseEntity.noContent().build();
        } catch (LotNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @PostMapping("/lots/{lotId}/levels")
    public ResponseEntity<LevelDto> createLevel(@PathVariable String lotId, @Valid @RequestBody LevelCreateRequest request) throws ConflictException, NotFoundException {
        try {
            LevelDto addedLevel = spaceService.addLevel(lotId, request.getNumber());
            URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{levelNumber}")
                .buildAndExpand(addedLevel.getNumber())
                .toUri();
            return ResponseEntity.created(location).body(addedLevel);
        } catch (LotNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @DeleteMapping("/lots/{lotId}/levels/{levelNumber}")
    public ResponseEntity<Void> deleteLevel(@PathVariable String lotId, @PathVariable Integer levelNumber) throws NotFoundException, DomainException {
        try {
            LevelDto removedLevel = spaceService.removeLevel(lotId, levelNumber);
            return ResponseEntity.noContent().build();
        } catch (LevelNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @PostMapping("/lots/{lotId}/levels/{levelNumber}/slots")
    public ResponseEntity<SlotDto> createSlot(
        @PathVariable String lotId,
        @PathVariable Integer levelNumber,
        @Valid @RequestBody SlotCreateRequest request) throws DomainException
    {
        SlotDto addedSlot = spaceService.addSlot(lotId, levelNumber, request.getType());
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{slotId}")
            .buildAndExpand(addedSlot.getId())
            .toUri();
        return ResponseEntity.created(location).body(addedSlot);
    }

    @PatchMapping("/lots/{lotId}/levels/{levelNumber}/slots/{slotId}")
    public ResponseEntity<SlotDto> updateSlot(
        @PathVariable String lotId,
        @PathVariable Integer levelNumber,
        @PathVariable int slotId,
        @Valid @RequestBody SlotUpdateRequest request) throws NotFoundException, DomainException
    {
        try {
            SlotDto updatedSlot = spaceService.changeSlotStatus(lotId, levelNumber, slotId, request.getStatus());
            return ResponseEntity.ok(updatedSlot);
        } catch (SlotNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @DeleteMapping("/lots/{lotId}/levels/{levelNumber}/slots/{slotId}")
    public ResponseEntity<Void> deleteSlot(@PathVariable String lotId, @PathVariable Integer levelNumber, @PathVariable int slotId) throws NotFoundException, DomainException {
        try {
            SlotDto removedSlot = spaceService.removeSlot(lotId, levelNumber, slotId);
            return ResponseEntity.noContent().build();
        } catch (SlotNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }
}