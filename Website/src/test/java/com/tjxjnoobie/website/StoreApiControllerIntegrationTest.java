package com.tjxjnoobie.website;

import com.fasterxml.jackson.databind.JsonNode;
import com.tjxjnoobie.store.persistence.entity.StoreOrderEntity;
import com.tjxjnoobie.store.persistence.repository.StoreOrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StoreApiControllerIntegrationTest extends WebsiteIntegrationTestSupport {

    @Autowired
    private StoreOrderRepository storeOrderRepository;

    @Test
    void rejectsInvalidRecipientUsername() throws Exception {
        mockMvc.perform(post("/api/v1/checkout/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "purchaserUsername": "StoreBot01",
                                  "recipientUsername": "bad name",
                                  "items": [{"packageSlug":"god-rank-lifetime","quantity":1}]
                                }
                                """))
                .andExpect(status().isBadRequest());

        assertTrue(storeOrderRepository.findAll().isEmpty());
    }

    @Test
    void createsGiftCheckoutForAnotherPlayer() throws Exception {
        String response = mockMvc.perform(post("/api/v1/checkout/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "purchaserUsername": "StoreBot01",
                                  "recipientUsername": "GiftBot02",
                                  "items": [{"packageSlug":"god-rank-lifetime","quantity":1}]
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode payload = objectMapper.readTree(response);
        String orderNumber = payload.get("orderNumber").asText();
        StoreOrderEntity order = storeOrderRepository.findByOrderNumber(orderNumber).orElseThrow();

        assertEquals("storebot01", order.getPurchaserUsernameSnapshot());
        assertEquals("giftbot02", order.getRecipientUsernameSnapshot());
        assertEquals(order.getRecipientPlayerAccount().getCurrentUsername(), "giftbot02");
    }
}
