package com.tjxjnoobie.listeners;

import com.tjxjnoobie.interfaces.BlockPlaceHandler;
import com.tjxjnoobie.interfaces.InterfaceManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.io.DataInputStream;
import java.io.IOException;

public class BlockPlaceListener implements Listener, BlockPlaceHandler {

    private BlockPlaceHandler blockPlaceHandler;

    public BlockPlaceListener(BlockPlaceHandler blockPlaceHandler) {
        this.blockPlaceHandler = blockPlaceHandler;
    }


    @Override
    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        blockPlaceHandler = InterfaceManager.getBlockPlaceHandler();
        blockPlaceHandler.onPlace(e);
    }

    @Override
    public void handleBlockPlace(DataInputStream dataIn) throws IOException {

    }
}