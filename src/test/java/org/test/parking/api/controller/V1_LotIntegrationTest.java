package org.test.parking.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.test.parking.lot.domain.Lot;
import org.test.parking.lot.repository.LotRepository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class V1_LotIntegrationTest {

    private static final String PARKING_SLOTS_URI_TEMPLATE = "/api/v1/parking/lots/{lotId}/levels/{level}/slots";
    private static final String PARKING_LEVELS_URI_TEMPLATE = "/api/v1/parking/lots/{lotId}/levels";
    private static final String PARKING_SLOT_URI_TEMPLATE = "/api/v1/parking/lots/{lotId}/levels/{level}/slots/{slotId}";
    private static final String PARKING_LEVEL_URI_TEMPLATE = "/api/v1/parking/lots/{lotId}/levels/{level}";
    private static final String PARKING_LOT_URI_TEMPLATE = "/api/v1/parking/lots/{lotId}";
    private static final String PARKING_LOTS_URI_TEMPLATE = "/api/v1/parking/lots";

    private static final String GIVEN_TEST_LOT_NAME = "Test Lot";
    private static final int GIVEN_LEVEL_NUMBER = 1;
    private static final String GIVEN_SLOT_TYPE = "COMPACT";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LotRepository lotRepository;

    private String lotId;

    @BeforeEach
    void cleanUp() {
        lotRepository.deleteAll();
    }

    @Test
    void shouldCreateLot() throws Exception {
        // given
        Map<String, Object> request = Map.of(
                "name", GIVEN_TEST_LOT_NAME
        );

        // when
        MvcResult result = mockMvc.perform(post(PARKING_LOTS_URI_TEMPLATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        // then (response)
        String response = result.getResponse().getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        String createdId = json.get("id").asText();

        assertNotNull(createdId);

        // then (DB)
        Optional<Lot> lot = lotRepository.findById(createdId);

        assertTrue(lot.isPresent());
        assertEquals(GIVEN_TEST_LOT_NAME, lot.get().getName());
    }

    @Test
    void shouldReturn409WhenLotNameAlreadyExists() throws Exception {
        // given
        Lot existing = new Lot(GIVEN_TEST_LOT_NAME);
        lotRepository.save(existing);

        Map<String, Object> request = Map.of(
                "name", GIVEN_TEST_LOT_NAME
        );

        // when + then
        mockMvc.perform(post(PARKING_LOTS_URI_TEMPLATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturn400WhenNameIsMissing() throws Exception {
        Map<String, Object> request = Map.of();

        mockMvc.perform(post(PARKING_LOTS_URI_TEMPLATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeleteLot() throws Exception {
        lotId = createLot(GIVEN_TEST_LOT_NAME);

        mockMvc.perform(delete(PARKING_LOT_URI_TEMPLATE, lotId))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete(PARKING_LOT_URI_TEMPLATE, lotId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateAndDeleteLevel() throws Exception {
        lotId = createLot(GIVEN_TEST_LOT_NAME);

        createLevel(lotId, GIVEN_LEVEL_NUMBER);

        mockMvc.perform(delete(PARKING_LEVEL_URI_TEMPLATE, lotId, GIVEN_LEVEL_NUMBER))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete(PARKING_LEVEL_URI_TEMPLATE, lotId, GIVEN_LEVEL_NUMBER))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateAndDeleteSlot() throws Exception {
        lotId = createLot(GIVEN_TEST_LOT_NAME);
        createLevel(lotId, GIVEN_LEVEL_NUMBER);

        int slotId = createSlot(lotId, GIVEN_LEVEL_NUMBER, GIVEN_SLOT_TYPE);

        mockMvc.perform(delete(PARKING_SLOT_URI_TEMPLATE,
                        lotId, GIVEN_LEVEL_NUMBER, slotId))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete(PARKING_SLOT_URI_TEMPLATE,
                        lotId, GIVEN_LEVEL_NUMBER, slotId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateSlotStatusToUnavailableAndBack() throws Exception {
        lotId = createLot(GIVEN_TEST_LOT_NAME);
        createLevel(lotId, GIVEN_LEVEL_NUMBER);
        int slotId = createSlot(lotId, GIVEN_LEVEL_NUMBER, GIVEN_SLOT_TYPE);

        Map<String, Object> request1 = Map.of("status", "UNAVAILABLE");

        mockMvc.perform(patch(PARKING_SLOT_URI_TEMPLATE,
                        lotId, GIVEN_LEVEL_NUMBER, slotId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UNAVAILABLE"));

        Map<String, Object> request2 = Map.of("status", "AVAILABLE");

        mockMvc.perform(patch(PARKING_SLOT_URI_TEMPLATE,
                        lotId, GIVEN_LEVEL_NUMBER, slotId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("AVAILABLE"));
    }

    @Test
    void shouldReturn404WhenCreatingLevelForNonExistingLot() throws Exception {
        Map<String, Object> request = Map.of("number", GIVEN_LEVEL_NUMBER);

        mockMvc.perform(post(PARKING_LEVELS_URI_TEMPLATE, "non-existing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // -------------------- HELPERS --------------------

    private String createLot(String name) throws Exception {
        Map<String, Object> request = Map.of("name", name);

        MvcResult result = mockMvc.perform(post(PARKING_LOTS_URI_TEMPLATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("id").asText();
    }

    private void createLevel(String lotId, int level) throws Exception {
        Map<String, Object> request = Map.of("number", level);

        mockMvc.perform(post(PARKING_LEVELS_URI_TEMPLATE, lotId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    private int createSlot(String lotId, int level, String type) throws Exception {
        Map<String, Object> request = Map.of("type", type);

        MvcResult result = mockMvc.perform(post(PARKING_SLOTS_URI_TEMPLATE, lotId, level)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("id").asInt();
    }
}
