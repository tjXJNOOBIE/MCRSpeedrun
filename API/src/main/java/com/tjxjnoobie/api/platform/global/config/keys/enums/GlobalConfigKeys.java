/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.platform.global.config.keys.enums;

import com.tjxjnoobie.api.platform.global.config.keys.interfaces.IDefaultConfigKey;

public enum GlobalConfigKeys implements IDefaultConfigKey {
    ENABLE_FEATURE("settings.enabled", Boolean.class ),
    MAX_PLAYERS("settings.maxPlayers", Integer.class),
    WELCOME_MESSAGE("ui.welcome", String.class);


    GlobalConfigKeys(String s, Class<?> clazz) {

    }


    @Override
    public IDefaultConfigKey getConfigKeyInstance() {
        return this;
    }

    @Override
    public Object getKey() {
        return null;
    }
}
