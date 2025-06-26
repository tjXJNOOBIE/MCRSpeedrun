package com.tjxjnoobie.listeners;

import com.tjxjnoobie.interfaces.ChatHandler;
import com.tjxjnoobie.interfaces.InterfaceManager;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;



public class ChatListener implements Listener, ChatHandler {


    @Override
    @EventHandler
    public void onChat(AsyncChatEvent e) {
        ChatHandler chatHandler = InterfaceManager.getChatHandler();
        chatHandler.onChat(e);
    }
}
