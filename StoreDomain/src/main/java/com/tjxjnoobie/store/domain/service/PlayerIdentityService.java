package com.tjxjnoobie.store.domain.service;

import com.tjxjnoobie.store.domain.model.UsernameResolution;

public interface PlayerIdentityService {
    UsernameResolution resolveUsername(String username);

    boolean isValidMinecraftUsername(String username);
}
