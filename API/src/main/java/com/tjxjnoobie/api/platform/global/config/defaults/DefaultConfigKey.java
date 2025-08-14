/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.platform.global.config.defaults;

import com.tjxjnoobie.api.platform.global.config.enums.ConfigDomain;
import com.tjxjnoobie.api.platform.global.config.enums.ConfigPathType;
import com.tjxjnoobie.api.platform.global.config.enums.ConfigType;
import com.tjxjnoobie.api.platform.global.config.keys.interfaces.IDefaultConfigKey;

import java.nio.file.Path;
import java.util.Objects;

public class DefaultConfigKey<T> implements IDefaultConfigKey<T> {

    private final Path path;
    private final Class<T> type;
    private final ConfigDomain domain;
    private final ConfigPathType pathType;
    private final ConfigType configType; // ✅ new
    private final boolean custom;

    public DefaultConfigKey(ConfigDomain domain, Path path, ConfigPathType pathType, Class<T> type, ConfigType configType, boolean custom) {
        this.domain = domain;
        this.path = path;
        this.pathType = pathType;
        this.type = type;
        this.configType = configType;
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
    public IDefaultConfigKey<T> getConfigKeyInstance() {
        return null;
    }

    @Override
    public T getKey() {
        return null;
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public ConfigPathType getPathType() {
        return pathType;
    }
    @Override

    public ConfigType getConfigType() { return configType; }
    @Override

    public boolean isCustom() { return custom; }

    @Override
    public boolean isValid(Object value) {
        return type.isInstance(value);
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DefaultConfigKey)) return false;
        DefaultConfigKey<?> that = (DefaultConfigKey<?>) o;
        return domain == that.domain &&
                path.equals(that.path) &&
                type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(domain, path, type);
    }

    @Override
    public String toString() {
        return "DefaultConfigKey{" +
                "domain=" + domain +
                ", path='" + path + '\'' +
                ", type=" + type.getSimpleName() +
                '}';
    }
}
