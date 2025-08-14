/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.platform.global.config.enums;

import com.tjxjnoobie.api.platform.global.config.defaults.ConfigPathDefaults;
import com.tjxjnoobie.api.platform.global.config.keys.interfaces.IDefaultConfigKey;

import java.nio.file.Path;

public enum ConfigPathType {

    PATH_ROOT(ConfigPathDefaults.ROOT, false),
    PATH_PAPER_PLUGIN(ConfigPathDefaults.PAPER_PLUGIN_DIR, false),
    PATH_PAPER_USER(ConfigPathDefaults.PAPER_USER_DIR, false),
    PATH_CUSTOM(null, true); // null = defined later

    private final Path defaultPath;
    private final boolean isCustom;

    ConfigPathType(Path defaultPath, boolean isCustom ) {
        this.defaultPath = defaultPath;
        this.isCustom = isCustom;

    }

    public boolean isCustom() {
        return isCustom;
    }

    public Path resolvePath(IDefaultConfigKey<?> key) {
        return switch (this) {
            case PATH_ROOT -> key.getPath().getRoot();
            case PATH_PAPER_PLUGIN -> null;
            case PATH_PAPER_USER -> null;
            case PATH_CUSTOM -> key.getPath();
            default -> defaultPath.resolve(key.getPath().getFileName());
        };
    }
    public Path getDefaultPath() {
        return defaultPath;
    }
}
