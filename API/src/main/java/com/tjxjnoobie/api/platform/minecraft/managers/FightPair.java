/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.platform.minecraft.managers;

import java.util.UUID;

/**
 * FightPair – TODO: implement class functionality
 * Auto-generated skeleton by MondayGPT-style template
 *
 * @author TJ
 * @since 10/1/2025
 */
public record FightPair(UUID player1, UUID player2) {
    public FightPair(UUID player1, UUID player2) {
        // Always store UUIDs in consistent order to ensure equality works
        if (player1.compareTo(player2) < 0) {
            this.player1 = player1;
            this.player2 = player2;
        } else {
            this.player1 = player2;
            this.player2 = player1;
        }
    }

    public boolean contains(UUID playerUUID) {
        return player1.equals(playerUUID) || player2.equals(playerUUID);
    }

    public UUID getOther(UUID playerUUID) {
        if (player1.equals(playerUUID)) {
            return player2;
        } else if (player2.equals(playerUUID)) {
            return player1;
        }
        return null;
    }
}
