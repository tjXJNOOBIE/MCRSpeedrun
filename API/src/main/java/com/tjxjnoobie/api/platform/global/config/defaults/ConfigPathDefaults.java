/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.platform.global.config.defaults;

import java.nio.file.Path;

public final class ConfigPathDefaults {

    public static final Path ROOT = Path.of("/");
    public static final Path PAPER_PLUGIN_DIR = Path.of("plugins/YourPlugin");
    public static final Path PAPER_USER_DIR = Path.of(PAPER_PLUGIN_DIR + "/user");
    public static final Path PAPER_DATA_DIR = Path.of(PAPER_PLUGIN_DIR + "/data");
    public static final Path PAPER_LOG_DIR = Path.of(PAPER_PLUGIN_DIR + "/logs");
}
