package com.tjxjnoobie.core.Events;

import com.tjxjnoobie.api.enums.GameTypeEnum;
import com.tjxjnoobie.api.interfaces.IGameType;
import com.tjxjnoobie.api.interfaces.ILobbyStatsCache;
import com.tjxjnoobie.api.interfaces.IRankCache;
import com.tjxjnoobie.api.platform.global.annotations.PostConstruct;
import com.tjxjnoobie.api.platform.minecraft.core.interfaces.ChatHandler;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class ChatEvent implements Listener, ChatHandler, IRankCache, IGameType, ILobbyStatsCache {

    private final Map<String, String> chatFormats = new HashMap<>();
    private final Map<String, String> placeholders = new HashMap<>();



    @PostConstruct
    public void init() {
        loadChatFormants();
    }

    public void loadChatFormants() {
        chatFormats.put("defaultLobby", "{grade} {displayname}&f:&8 {message}");
        chatFormats.put("DonorLobby", "{grade} {displayname}&8:&f {message}");
        chatFormats.put("StaffLobby", "{grade} {displayname}&f:&b {message}");
    }


    @EventHandler
    @Override
    public void onChat(AsyncChatEvent e) {

        GameTypeEnum currentType = getGameType();

        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        String plainTextMessage = PlainTextComponentSerializer.plainText().serialize(e.message());
        String grade = "&8[&c" + getGlobalGrade(uuid) + "&8]";
        String template = "";

        // Create a fresh placeholders map for each event
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("displayname", player.getDisplayName());
        placeholders.put("message", plainTextMessage);
        placeholders.put("grade", grade);

        // Determine which template to use
        if (getCachedRank(uuid).equals("Member")) {
            template = chatFormats.get("defaultLobby");
        } else if (getCachedPowerLevel(uuid) <= 500) {
            template = chatFormats.get("DonorLobby");
        } else if (isStaff(uuid)) {
            template = chatFormats.get("StaffLobby");
        }

        // Format the message using the placeholders
        String formattedMessage = formatChatMessage(template, placeholders);

        // Deserialize legacy color codes into a Component using Adventure
        Component formattedComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(formattedMessage);

        // Override the event's message renderer to show only our formatted message
        e.renderer((source, sourceDisplayName, message, viewer) -> formattedComponent);
    }

    private String formatChatMessage(String template, Map<String, String> placeholders) {
        // This expects keys like "displayname", "message", etc.
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            template = template.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return template;
    }
}