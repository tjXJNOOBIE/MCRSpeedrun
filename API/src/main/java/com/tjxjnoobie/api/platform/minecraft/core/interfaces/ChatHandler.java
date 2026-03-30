package com.tjxjnoobie.api.platform.minecraft.core.interfaces;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public interface ChatHandler extends Listener {


    @EventHandler
    void onChat(AsyncChatEvent e);
}