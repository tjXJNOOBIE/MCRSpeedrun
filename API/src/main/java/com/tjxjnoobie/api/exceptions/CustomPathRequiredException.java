package com.tjxjnoobie.api.exceptions;

import com.tjxjnoobie.api.platform.global.config.keys.interfaces.IDefaultConfigKey;

public class CustomPathRequiredException extends RuntimeException {
    public CustomPathRequiredException(IDefaultConfigKey<?> key) {
        super("Custom path required for key: " + key.getKey());
    }
}
