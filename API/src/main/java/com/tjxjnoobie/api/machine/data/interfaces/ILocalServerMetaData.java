/*
 * TJVD License (TJ Valentine’s Discretionary License) — Version 1.0 (2025)
 *
 * Copyright (c) 2025 Taheesh Valentine
 *
 * This source code is protected under the TJVD License.
 * SEE LICENSE.TXT
 */

package com.tjxjnoobie.api.machine.data.interfaces;

public interface ILocalServerMetaData {

    default String getServerID() {
        return "";
    }

    default void setServerID(String serverID) {
    }

    default String getMinecraftInGamePrefix(){
        return "";
    }

    default String getMinecraftStaffInGamePrefix(){
        return "";
    }

    default String getGameID() {
        return "";
    }

    default void setGameID(String gameID) {
    }

    default String getMinecraftPrefix(){
        return "";
    }
}
