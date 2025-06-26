package com.tjxjnoobie.interfaces;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;

public interface ChatHandler {


    @EventHandler
    void onChat(AsyncChatEvent e);
}
