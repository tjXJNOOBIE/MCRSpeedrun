/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 *
 * No public use, distribution, or modification is permitted without express,
 * written, and verifiable consent from the project founder.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.interfaces;

public interface ICacheStats extends com.tjxjnoobie.api.dependency.IDependencyInjectableInterface {
    int getTotalEntries();

    int getValidEntries();

    int getExpiredEntries();

}
