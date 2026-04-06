package com.tjxjnoobie.store.domain.service.implementation;

import com.tjxjnoobie.store.domain.model.UsernameResolution;
import com.tjxjnoobie.store.domain.service.PlayerIdentityService;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

@Component
public class DefaultPlayerIdentityService implements PlayerIdentityService {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]{3,16}$");

    @Override
    public UsernameResolution resolveUsername(String username) {
        String trimmed = trim(username);
        String normalized = normalize(trimmed);
        UUID offlineUuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + trimmed)
                .getBytes(StandardCharsets.UTF_8));
        return new UsernameResolution(offlineUuid, normalized);
    }

    @Override
    public boolean isValidMinecraftUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }

    private String normalize(String username) {
        return trim(username).toLowerCase(Locale.ROOT);
    }

    private String trim(String username) {
        return username == null ? "" : username.trim();
    }
}
