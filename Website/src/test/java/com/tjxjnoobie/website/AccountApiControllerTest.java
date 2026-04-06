package com.tjxjnoobie.website;

import com.fasterxml.jackson.databind.JsonNode;
import com.tjxjnoobie.store.domain.service.PlayerIdentityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccountApiControllerTest extends WebsiteIntegrationTestSupport {

    @Autowired
    private PlayerIdentityService playerIdentityService;

    @Test
    void ownershipChallengeAndSessionFlowWorks() throws Exception {
        String challengeResponse = mockMvc.perform(post("/api/v1/account/verification/challenges")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"StoreBot01"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.readyForGameVerification").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode challenge = objectMapper.readTree(challengeResponse);
        String code = challenge.get("code").asText();
        String playerUuid = playerIdentityService.resolveUsername("StoreBot01").uuid().toString();

        mockMvc.perform(post("/api/v1/integration/ownership/verify")
                        .header("Authorization", "Bearer " + integrationToken(com.tjxjnoobie.store.integration.auth.IntegrationScope.OWNERSHIP_VERIFY))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "code":"%s",
                                  "playerUuid":"%s",
                                  "playerUsername":"StoreBot01"
                                }
                                """.formatted(code, playerUuid)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verified").value(true));

        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(post("/api/v1/account/session")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"code":"%s"}
                                """.formatted(code)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/account/orders").session(session))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/account/orders"))
                .andExpect(status().isUnauthorized());

        assertEquals("storebot01", findPlayerByUsername("storebot01").getCurrentUsername());
    }
}
