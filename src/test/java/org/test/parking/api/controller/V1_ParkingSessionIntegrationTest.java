package org.test.parking.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.test.parking.lot.repository.LotRepository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.test.parking.session.repository.SessionRepository;

@SpringBootTest
@AutoConfigureMockMvc
class V1_ParkingSessionIntegrationTest {

    private static final String PARKING_SLOTS_URI_TEMPLATE = "/api/v1/parking/lots/{lotId}/levels/{level}/slots";
    private static final String PARKING_LEVELS_URI_TEMPLATE = "/api/v1/parking/lots/{lotId}/levels";
    private static final String PARKING_LOTS_URI_TEMPLATE = "/api/v1/parking/lots";
    private static final String SESSION_CHECKOUT_URI_TEMPLATE = "/api/v1/sessions/{sessionId}/check-out";
    private static final String SESSION_LOT_URI_TEMPLATE = "/api/v1/sessions/lots/{lotId}";

    private static final String GIVEN_VEHICLE_TYPE = "CAR";
    private static final String GIVEN_VEHICLE_PLATE_1 = "AA1234BB";
    private static final String GIVEN_VEHICLE_PLATE_2 = "AA1111AA";
    private static final String GIVEN_VEHICLE_PLATE_3 = "BB2222BB";
    private static final String GIVEN_SLOT_TYPE = "COMPACT";
    private static final int GIVEN_LEVEL_NUMBER = 1;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LotRepository lotRepository;

    @Autowired
    private SessionRepository sessionRepository;

    private String lotId;

    @BeforeEach
    void cleanUp() {
        lotRepository.deleteAll();
        sessionRepository.deleteAll();
    }

    @Test
    void shouldCheckInAndCheckOutVehicleSuccessfully() throws Exception {
        lotId = createLot();
        createLevel(lotId, GIVEN_LEVEL_NUMBER);
        createSlot(lotId, GIVEN_LEVEL_NUMBER, GIVEN_SLOT_TYPE);

        Map<String, Object> checkInRequest = Map.of(
                "vehicle", Map.of(
                        "licensePlate", GIVEN_VEHICLE_PLATE_1,
                        "type", GIVEN_VEHICLE_TYPE
                )
        );

        MvcResult checkInResult = mockMvc.perform(
                        post(SESSION_LOT_URI_TEMPLATE, lotId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(checkInRequest))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.licensePlate").value(GIVEN_VEHICLE_PLATE_1))
                .andReturn();

        JsonNode checkInJson = objectMapper.readTree(checkInResult.getResponse().getContentAsString());
        String sessionId = checkInJson.get("id").asText();

        Thread.sleep(1000);

        MvcResult checkOutResult = mockMvc.perform(
                        post(SESSION_CHECKOUT_URI_TEMPLATE, sessionId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.licensePlate").value(GIVEN_VEHICLE_PLATE_1))
                .andExpect(jsonPath("$.durationMinutes").isNumber())
                .andExpect(jsonPath("$.fee").isNumber())
                .andReturn();

        JsonNode checkOutJson = objectMapper.readTree(checkOutResult.getResponse().getContentAsString());

        assertNotNull(checkOutJson.get("exitTime"));
        assertTrue(checkOutJson.get("durationMinutes").asInt() >= 0);
        assertTrue(checkOutJson.get("fee").asDouble() >= 0.0);
    }

    @Test
    void shouldNotAllowDuplicateCheckIn() throws Exception {
        lotId = createLot();
        createLevel(lotId, GIVEN_LEVEL_NUMBER);
        createSlot(lotId, GIVEN_LEVEL_NUMBER, GIVEN_SLOT_TYPE);

        Map<String, Object> request = Map.of(
                "vehicle", Map.of(
                        "licensePlate", GIVEN_VEHICLE_PLATE_1,
                        "type", GIVEN_VEHICLE_TYPE
                )
        );

        mockMvc.perform(post(SESSION_LOT_URI_TEMPLATE, lotId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post(SESSION_LOT_URI_TEMPLATE, lotId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnErrorWhenSessionAlreadyClosed() throws Exception {
        lotId = createLot();
        createLevel(lotId, GIVEN_LEVEL_NUMBER);
        createSlot(lotId, GIVEN_LEVEL_NUMBER, GIVEN_SLOT_TYPE);
    
        Map<String, Object> request = Map.of(
                "vehicle", Map.of(
                        "licensePlate", GIVEN_VEHICLE_PLATE_1,
                        "type", GIVEN_VEHICLE_TYPE
                )
        );
    
        MvcResult result = mockMvc.perform(post(SESSION_LOT_URI_TEMPLATE, lotId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();
    
        String sessionId = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("id").asText();
    
        mockMvc.perform(post(SESSION_CHECKOUT_URI_TEMPLATE, sessionId))
                .andExpect(status().isOk());
    
        mockMvc.perform(post(SESSION_CHECKOUT_URI_TEMPLATE, sessionId))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnOnlyActiveSessions() throws Exception {
        String lotId = createLot();
        createLevel(lotId, GIVEN_LEVEL_NUMBER);

        createSlot(lotId, GIVEN_LEVEL_NUMBER, GIVEN_SLOT_TYPE);
        createSlot(lotId, GIVEN_LEVEL_NUMBER, GIVEN_SLOT_TYPE);

        Map<String, Object> vehicle1 = Map.of(
                "vehicle", Map.of(
                        "licensePlate", GIVEN_VEHICLE_PLATE_2,
                        "type", GIVEN_VEHICLE_TYPE
                )
        );

        Map<String, Object> vehicle2 = Map.of(
                "vehicle", Map.of(
                        "licensePlate", GIVEN_VEHICLE_PLATE_3,
                        "type", GIVEN_VEHICLE_TYPE
                )
        );

        String sessionId1 = createSession(lotId, vehicle1);
        String sessionId2 = createSession(lotId, vehicle2);

        mockMvc.perform(post(SESSION_CHECKOUT_URI_TEMPLATE, sessionId1))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(
                        get(SESSION_LOT_URI_TEMPLATE, lotId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());

        assertEquals(1, json.size());

        JsonNode activeSession = json.get(0);

        assertEquals(GIVEN_VEHICLE_PLATE_3,
                activeSession.get("licensePlate").asText());

        assertEquals("ACTIVE",
                activeSession.get("status").asText());
    }

    // -------------------- HELPERS --------------------

    private String createLot() throws Exception {
        Map<String, Object> request = Map.of("name", "Test Lot");

        MvcResult result = mockMvc.perform(post(PARKING_LOTS_URI_TEMPLATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("id").asText();
    }

    private void createLevel(String lotId, int level) throws Exception {
        Map<String, Object> request = Map.of("number", level);

        mockMvc.perform(post(PARKING_LEVELS_URI_TEMPLATE, lotId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    private void createSlot(String lotId, int level, String type) throws Exception {
        Map<String, Object> request = Map.of("type", type);

        mockMvc.perform(post(PARKING_SLOTS_URI_TEMPLATE, lotId, level)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    private String createSession(String lotId, Map<String, Object> request) throws Exception {
        MvcResult result = mockMvc.perform(
                        post(SESSION_LOT_URI_TEMPLATE, lotId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andReturn();
    
        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("id").asText();
    }
}
