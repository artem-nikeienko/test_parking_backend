package org.test.parking.controller;

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
import org.test.parking.controller.request.LevelCreateRequest;
import org.test.parking.controller.request.LotCreateRequest;
import org.test.parking.controller.request.SlotCreateRequest;
import org.test.parking.controller.request.SlotUpdateRequest;
import org.test.parking.controller.response.CreateOperationResponse;
import org.test.parking.controller.response.DeleteOperationResponse;
import org.test.parking.controller.response.UpdateOperationResponse;
import org.test.parking.exception.ConflictException;
import org.test.parking.exception.NotFoundException;
import org.test.parking.exception.domain.LevelNotFoundException;
import org.test.parking.exception.domain.LotNotFoundException;
import org.test.parking.exception.domain.RestrictedLotOperationException;
import org.test.parking.exception.domain.SlotNotFoundException;
import org.test.parking.service.parking.ParkingSpaceService;

import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/api/v1/parking")
public class ParkingLotController {

    private final ParkingSpaceService spaceService;

    public ParkingLotController(ParkingSpaceService spaceService) {
        this.spaceService = spaceService;
    }

    @PostMapping("/lots")
    public ResponseEntity<CreateOperationResponse> createLot(@Valid @RequestBody LotCreateRequest request) throws ConflictException {
        //ASSUMPTION: Request object stays at the Controller layer, Service layer gets only its attributes needed for business logic to avoid excessive coupling between layers.
        String lotId = spaceService.addLot(request.getName());
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{lotId}")
            .buildAndExpand(lotId)
            .toUri();
        CreateOperationResponse response = CreateOperationResponse.builder().createdEntityId(lotId).build();
        return ResponseEntity.created(location).body(response);
    }

    @DeleteMapping("/lots/{lotId}")
    public ResponseEntity<DeleteOperationResponse> deleteLot(@PathVariable String lotId) throws NotFoundException {
        DeleteOperationResponse.DeleteOperationResponseBuilder responseBuilder = DeleteOperationResponse.builder();
        try {
            spaceService.removeLot(lotId);
            responseBuilder
                .success(true);
        } catch (RestrictedLotOperationException e) {
            responseBuilder
                .success(false)
                .operationDetails(e.getMessage());
        } catch (LotNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
        return ResponseEntity.ok(responseBuilder.build());
    }

    @PostMapping("/lots/{lotId}/levels")
    public ResponseEntity<CreateOperationResponse> createLevel(@PathVariable String lotId, @Valid @RequestBody LevelCreateRequest request) throws ConflictException, NotFoundException {
        //TODO: Explicitly tell about Lombok annotations processing in documentation (README.md) to avoid confusion for developers who are not familiar with Lombok, because it may lead to confusion about where the getters and setters are coming from, etc.
        try {
            Integer addedLevelId = spaceService.addLevel(lotId, request.getNumber());
            CreateOperationResponse response = CreateOperationResponse.builder().createdEntityId(addedLevelId.toString()).build();
            URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{levelNumber}")
                .buildAndExpand(addedLevelId)
                .toUri();
            return ResponseEntity.created(location).body(response);
        } catch (LotNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @DeleteMapping("/lots/{lotId}/levels/{levelNumber}")
    public ResponseEntity<DeleteOperationResponse> deleteLevel(@PathVariable String lotId, @PathVariable Integer levelNumber) throws NotFoundException {
        DeleteOperationResponse.DeleteOperationResponseBuilder responseBuilder = DeleteOperationResponse.builder();
        try {
            spaceService.removeLevel(lotId, levelNumber);
            responseBuilder
                .success(true);
        } catch (RestrictedLotOperationException e) {
            responseBuilder
                .success(false)
                .operationDetails(e.getMessage());
        } catch (LotNotFoundException | LevelNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
        return ResponseEntity.ok(responseBuilder.build());
    }

    @PostMapping("/lots/{lotId}/levels/{levelNumber}/slots")
    //TODO: Use some Response object instead of returning Slot directly, because we may want to return additional info in the future, such as the URL of the created slot, etc.
    //TODO: align openapi.yaml with controllers
    public ResponseEntity<CreateOperationResponse> createSlot(
        @PathVariable String lotId,
        @PathVariable Integer levelNumber,
        @Valid @RequestBody SlotCreateRequest request) throws NotFoundException
    {
        try {
            Integer addedSlotId = spaceService.addSlot(lotId, levelNumber, request.getType());
            CreateOperationResponse response = CreateOperationResponse.builder().createdEntityId(addedSlotId.toString()).build();
            URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{slotId}")
                .buildAndExpand(addedSlotId)
                .toUri();
            return ResponseEntity.created(location).body(response);
        } catch (LotNotFoundException | LevelNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @PatchMapping("/lots/{lotId}/levels/{levelNumber}/slots/{slotId}")
    public ResponseEntity<UpdateOperationResponse> updateSlot(
        @PathVariable String lotId,
        @PathVariable Integer levelNumber,
        @PathVariable int slotId,
        @Valid @RequestBody SlotUpdateRequest request) throws NotFoundException
    {
        UpdateOperationResponse.UpdateOperationResponseBuilder responseBuilder = UpdateOperationResponse.builder();
        try {
            spaceService.changeSlotStatus(lotId, levelNumber, slotId, request.getStatus());
            responseBuilder
                .success(true);
        } catch (RestrictedLotOperationException e) {
            responseBuilder
                .success(false)
                .operationDetails(e.getMessage());
        } catch (LotNotFoundException | LevelNotFoundException | SlotNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
        return ResponseEntity.ok(responseBuilder.build());
    }

    @DeleteMapping("/lots/{lotId}/levels/{levelNumber}/slots/{slotId}")
    public ResponseEntity<DeleteOperationResponse> deleteSlot(@PathVariable String lotId, @PathVariable Integer levelNumber, @PathVariable int slotId) throws NotFoundException {
        DeleteOperationResponse.DeleteOperationResponseBuilder responseBuilder = DeleteOperationResponse.builder();
        try {
            spaceService.removeSlot(lotId, levelNumber, slotId);
            responseBuilder
                .success(true);
        } catch (RestrictedLotOperationException e) {
            responseBuilder
                .success(false)
                .operationDetails(e.getMessage());
        } catch (LotNotFoundException | LevelNotFoundException | SlotNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
        return ResponseEntity.ok(responseBuilder.build());
    }
}