package com.tjxjnoobie.api.interfaces;


import org.bukkit.entity.Player;

public interface MainInterFace {

    void sendPluginMessage(byte[] data);
    void onPluginMessageReceived(String channel, Player player, byte[] message);

}


