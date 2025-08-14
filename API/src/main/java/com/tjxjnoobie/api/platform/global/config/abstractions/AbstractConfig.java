/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.platform.global.config.abstractions;

import com.tjxjnoobie.api.platform.global.config.enums.ConfigDomain;
import com.tjxjnoobie.api.platform.global.config.enums.ConfigPathType;
import com.tjxjnoobie.api.platform.global.config.keys.interfaces.IDefaultConfigKey;

import java.nio.file.Path;

public abstract class AbstractConfig<T> implements IDefaultConfigKey<T> {

    private final Path path;
    private final Class<T> type;
    private final ConfigDomain domain;
    private final ConfigPathType pathType;
    private final boolean custom;

    protected AbstractConfig(Path path, Class<T> type, ConfigDomain domain, ConfigPathType pathType, boolean custom) {
        this.path = path;
        this.type = type;
        this.domain = domain;
        this.pathType = pathType;
        this.custom = custom;
    }


    @Override
    public ConfigDomain getDomain() {
        return domain;
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public boolean isValid(Object value) {
        return false;
    }


    @Override
    public ConfigPathType getPathType() {
        return null;
    }
}
