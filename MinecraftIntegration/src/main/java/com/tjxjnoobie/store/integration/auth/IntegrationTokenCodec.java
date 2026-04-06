package com.tjxjnoobie.store.integration.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Clock;
import java.util.Base64;

public class IntegrationTokenCodec {

    private static final String HMAC_SHA_256 = "HmacSHA256";

    private final ObjectMapper objectMapper;
    private final Clock clock;
    private final String sharedSecret;

    public IntegrationTokenCodec() {
        this("", Clock.systemUTC());
    }

    public IntegrationTokenCodec(String sharedSecret) {
        this(sharedSecret, Clock.systemUTC());
    }

    public IntegrationTokenCodec(String sharedSecret, Clock clock) {
        this.sharedSecret = sharedSecret;
        this.clock = clock;
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public String issue(IntegrationTokenClaims claims) {
        try {
            String payload = objectMapper.writeValueAsString(claims);
            String encodedPayload = encode(payload.getBytes(StandardCharsets.UTF_8));
            String signature = encode(sign(encodedPayload));
            return encodedPayload + "." + signature;
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to serialize integration token", exception);
        }
    }

    public IntegrationTokenClaims verify(String token) {
        String[] segments = token.split("\\.");
        if (segments.length != 2) {
            throw new IllegalArgumentException("Malformed integration token");
        }

        String encodedPayload = segments[0];
        String encodedSignature = segments[1];
        String expectedSignature = encode(sign(encodedPayload));
        if (!MessageDigest.isEqual(expectedSignature.getBytes(StandardCharsets.UTF_8),
                encodedSignature.getBytes(StandardCharsets.UTF_8))) {
            throw new IllegalArgumentException("Invalid integration token signature");
        }

        try {
            IntegrationTokenClaims claims = objectMapper.readValue(decode(encodedPayload), IntegrationTokenClaims.class);
            long now = clock.instant().getEpochSecond();
            if (claims.expiresAtEpochSecond() < now) {
                throw new IllegalArgumentException("Integration token expired");
            }
            return claims;
        } catch (Exception exception) {
            throw new IllegalArgumentException("Malformed integration token payload", exception);
        }
    }

    private byte[] sign(String value) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA_256);
            mac.init(new SecretKeySpec(sharedSecret.getBytes(StandardCharsets.UTF_8), HMAC_SHA_256));
            return mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Unable to sign integration token", exception);
        }
    }

    private String encode(byte[] value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value);
    }

    private byte[] decode(String value) {
        return Base64.getUrlDecoder().decode(value);
    }
}
