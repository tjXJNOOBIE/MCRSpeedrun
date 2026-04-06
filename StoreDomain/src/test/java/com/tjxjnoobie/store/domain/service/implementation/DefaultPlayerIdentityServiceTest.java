package com.tjxjnoobie.store.domain.service.implementation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultPlayerIdentityServiceTest {

    private final DefaultPlayerIdentityService service = new DefaultPlayerIdentityService();

    @Test
    void resolvesOfflineModeUuidDeterministically() {
        String first = service.resolveUsername("Notch").uuid().toString();
        String second = service.resolveUsername("Notch").uuid().toString();
        assertEquals(first, second);
    }

    @Test
    void preservesUsernameCaseInOfflineModeUuidDerivation() {
        String mixedCase = service.resolveUsername("StoreBot01").uuid().toString();
        String lowerCase = service.resolveUsername("storebot01").uuid().toString();
        assertNotEquals(mixedCase, lowerCase);
        assertEquals("storebot01", service.resolveUsername("StoreBot01").normalizedUsername());
    }

    @Test
    void validatesMinecraftUsernames() {
        assertTrue(service.isValidMinecraftUsername("Builder_123"));
        assertFalse(service.isValidMinecraftUsername("bad name"));
    }
}
