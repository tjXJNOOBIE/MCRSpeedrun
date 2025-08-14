package com.tjxjnoobie.api.interfaces;

import org.bukkit.event.block.BlockPlaceEvent;

import java.io.DataInputStream;
import java.io.IOException;

public interface BlockPlaceHandler {
    /**
     * Handles block placement events.
     *
     * @param e The BlockPlaceEvent triggered when a block is placed.
     */
    void onPlace(BlockPlaceEvent e);

    /**
     * Handles incoming block placement data.
     *
     * @param dataIn The DataInputStream containing block placement data.
     * @throws IOException If an I/O error occurs.
     */
    void handleBlockPlace(DataInputStream dataIn) throws IOException;
}