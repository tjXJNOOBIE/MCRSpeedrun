package com.tjxjnoobie.core.Events;

import com.tjxjnoobie.API.cache.LobbyStatsCache;
import com.tjxjnoobie.API.cache.RankCache;
import com.tjxjnoobie.API.managers.GameType;
import com.tjxjnoobie.API.GlobalContext;
import com.tjxjnoobie.enums.GameTypeEnum;
import com.tjxjnoobie.interfaces.ChatHandler;
import com.tjxjnoobie.interfaces.IGameType;
import com.tjxjnoobie.interfaces.ILobbyStatsCache;
import com.tjxjnoobie.interfaces.IRankCache;
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


public class ChatEvent implements Listener, ChatHandler {

    private final GlobalContext globalContext;
    private final Map<String, String> chatFormats = new HashMap<>();
    private final Map<String, String> placeholders = new HashMap<>();


    public ChatEvent(GlobalContext globalContext) {
        this.globalContext = globalContext;
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
        IRankCache rankCache = globalContext.getRankCache();
        IGameType gameType = globalContext.getGameType();
        ILobbyStatsCache lobbyStatsCache = globalContext.getLobbyStatsCache();
        GameTypeEnum currentType = gameType.getGameType();

        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        String plainTextMessage = PlainTextComponentSerializer.plainText().serialize(e.message());
        String grade = "&8[&c" + lobbyStatsCache.getGlobalGrade(uuid) + "&8]";
        String template = "";

        // Create a fresh placeholders map for each event
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("displayname", player.getDisplayName());
        placeholders.put("message", plainTextMessage);
        placeholders.put("grade", grade);

        // Determine which template to use
        if (rankCache.getRank(uuid).equals("Member")) {
            template = chatFormats.get("defaultLobby");
        } else if (rankCache.getPowerLevel(uuid) <= 500) {
            template = chatFormats.get("DonorLobby");
        } else if (rankCache.isStaff(uuid)) {
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




