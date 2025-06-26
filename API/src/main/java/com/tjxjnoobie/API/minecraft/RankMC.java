package com.tjxjnoobie.API.minecraft;

import com.tjxjnoobie.API.cache.RankCache;
import com.tjxjnoobie.API.velocity.Rank;
import com.tjxjnoobie.interfaces.IRank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Supplier;

public abstract class RankMC implements IRank{

    private final Rank rank;
    private final RankCache rankCache;

    public RankMC(Rank rank, RankCache rankCache) {
        this.rank = rank;
        this.rankCache = rankCache;
    }

    @Override
    public void setDisplayName(Player player) throws SQLException {
        UUID uuid = player.getUniqueId();
        String name = player.getName();
        if(rankCache.getRank(uuid).equals("Developer")){
            String[] parts = "Developer ".split("");
            String[] parts1 = player.getName().split("");
            StringBuffer buffer = new StringBuffer();
            StringBuffer buffer2 = new StringBuffer();
            Supplier<ChatColor> prefixColorFunction = getRandomColorFunction();
            Supplier<ChatColor> nameColorFunction = getRandomColorFunction();

            for (String s : parts) {
                ChatColor randomColor = prefixColorFunction.get();
                buffer.append(randomColor).append(ChatColor.BOLD).append(s);
            }

            for (String s : parts1) {
                ChatColor randomColor = nameColorFunction.get();
                buffer2.append(randomColor).append(ChatColor.BOLD).append(s);
            }
            String DevDisplay = ChatColor.BOLD + "" + buffer.toString() + ChatColor.RESET;
            String DevDisplayName = ChatColor.BOLD + "" + buffer2.toString() + ChatColor.RESET;
            setPlayerNameTag(player,DevDisplay,"");
            player.setDisplayName(DevDisplayName);
            player.setPlayerListName(DevDisplayName);



        }else if (rankCache.getRank(uuid).equals("Owner")){
            player.setDisplayName("§4§l"+name);
        }else if(rankCache.getRank(uuid).equals("HeadAdmin")){
            player.setDisplayName("§4"+name);
        }else if(rankCache.getRank(uuid).equals("SrMod")){
            player.setDisplayName("§c§l"+name);
        }else if(rankCache.getRank(uuid).equals("Mod")){
            player.setDisplayName("§c"+name);
        }else if(rankCache.getRank(uuid).equals("Partner")){
            player.setDisplayName("§d"+name);

        }
    }
    private Supplier<ChatColor> getRandomColorFunction() {
        List<Supplier<ChatColor>> colorFunctions = Arrays.asList(
                this::getNegativeColor,
                this::getRainbowPlusColor,
                this::getRainbowColor
        );
        Random random = new Random();
        return colorFunctions.get(random.nextInt(colorFunctions.size()));
    }
    public ChatColor getRandomColor(){
        Random random = new Random();
        int selectColor = random.nextInt(3);

        ArrayList<ChatColor> ChatColors = new ArrayList<ChatColor>();
        ArrayList<ChatColor> ChatColors_Negative = new ArrayList<ChatColor>();
        ArrayList<ChatColor> ChatColors_Rainbow = new ArrayList<ChatColor>();
        if(selectColor== 0) {
            ChatColors.add(ChatColor.AQUA);
            ChatColors.add(ChatColor.RED);
            ChatColors.add(ChatColor.GOLD);
            ChatColors.add(ChatColor.GREEN);
            ChatColors.add(ChatColor.LIGHT_PURPLE);
            ChatColors.add(ChatColor.YELLOW);
            ChatColors.add(ChatColor.DARK_PURPLE);
            return ChatColors.get(random.nextInt(ChatColors.size()));
        }else if(selectColor==1){
            ChatColors_Negative.add(ChatColor.AQUA);
            ChatColors_Negative.add(ChatColor.BLACK);
            ChatColors_Negative.add(ChatColor.DARK_BLUE);
            ChatColors_Negative.add(ChatColor.DARK_AQUA);
            ChatColors_Negative.add(ChatColor.BLUE);
            ChatColors_Negative.add(ChatColor.GRAY);
            return ChatColors_Negative.get(random.nextInt(ChatColors_Negative.size()));


        }else if(selectColor== 2) {
            ChatColors_Rainbow.add(ChatColor.AQUA);
            ChatColors_Rainbow.add(ChatColor.RED);
            ChatColors_Rainbow.add(ChatColor.GOLD);
            ChatColors_Rainbow.add(ChatColor.YELLOW);
            ChatColors_Rainbow.add(ChatColor.BLUE);
            ChatColors_Rainbow.add(ChatColor.LIGHT_PURPLE);
            ChatColors_Rainbow.add(ChatColor.GREEN);
           return ChatColors_Rainbow.get(random.nextInt(ChatColors_Rainbow.size()));

        }
        return null;
    }
    public ChatColor getRainbowColor() {
        Random random = new Random();
        ArrayList<ChatColor> ChatColors = new ArrayList<ChatColor>();
        ChatColors.add(ChatColor.AQUA);
        ChatColors.add(ChatColor.RED);
        ChatColors.add(ChatColor.GOLD);
        ChatColors.add(ChatColor.GREEN);
        ChatColors.add(ChatColor.LIGHT_PURPLE);
        ChatColors.add(ChatColor.YELLOW);
        ChatColors.add(ChatColor.DARK_PURPLE);
        return ChatColors.get(random.nextInt(ChatColors.size()));
    }
    public ChatColor getNegativeColor() {
        Random random = new Random();
        ArrayList<ChatColor> ChatColors_Negative = new ArrayList<ChatColor>();
        ChatColors_Negative.add(ChatColor.AQUA);
        ChatColors_Negative.add(ChatColor.BLACK);
        ChatColors_Negative.add(ChatColor.DARK_BLUE);
        ChatColors_Negative.add(ChatColor.DARK_AQUA);
        ChatColors_Negative.add(ChatColor.BLUE);
        ChatColors_Negative.add(ChatColor.GRAY);
        return ChatColors_Negative.get(random.nextInt(ChatColors_Negative.size()));
    }
    public ChatColor getRainbowPlusColor() {
        Random random = new Random();

        ArrayList<ChatColor> ChatColors_Rainbow = new ArrayList<ChatColor>();
        ChatColors_Rainbow.add(ChatColor.AQUA);
        ChatColors_Rainbow.add(ChatColor.RED);
        ChatColors_Rainbow.add(ChatColor.GOLD);
        ChatColors_Rainbow.add(ChatColor.GREEN);
        ChatColors_Rainbow.add(ChatColor.LIGHT_PURPLE);
        ChatColors_Rainbow.add(ChatColor.YELLOW);
        ChatColors_Rainbow.add(ChatColor.BLUE);
        return ChatColors_Rainbow.get(random.nextInt(ChatColors_Rainbow.size()));
    }

    public void setPlayerNameTag(Player player, String prefix, String suffix) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Team team = scoreboard.getTeam(player.getName());

        // Create team if it doesn't exist
        if (team == null) {
            team = scoreboard.registerNewTeam(player.getName());
        }

        // Set prefix and suffix
        team.setPrefix(prefix);
        team.setSuffix(suffix);
        team.addEntry(player.getName());

        // Apply the scoreboard to the player
        player.setScoreboard(scoreboard);
    }

    private static void refreshPlayer(Player player) {
        try {
            // Get the ServerPlayer class
            Class<?> serverPlayerClass = Class.forName("net.minecraft.server.level.ServerPlayer");
            Class<?> connectionClass = Class.forName("net.minecraft.server.network.ServerGamePacketListenerImpl");
            Class<?> packetClass = Class.forName("net.minecraft.network.protocol.Packet");
            Class<?> playerInfoPacketClass = Class.forName("net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket");
            Class<?> playerInfoActionClass = Class.forName("net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket$Action");

            // Get the ServerPlayer instance using reflection
            Method getHandleMethod = player.getClass().getDeclaredMethod("getHandle");
            Object serverPlayer = getHandleMethod.invoke(player);

            // Get the player's connection
            Field connectionField = serverPlayerClass.getDeclaredField("c"); // Check Mojang mappings for actual field name
            connectionField.setAccessible(true);
            Object playerConnection = connectionField.get(serverPlayer);

            // Get REMOVE_PLAYER and ADD_PLAYER actions
            Object removePlayerAction = Enum.valueOf((Class<Enum>) playerInfoActionClass, "REMOVE_PLAYER");
            Object addPlayerAction = Enum.valueOf((Class<Enum>) playerInfoActionClass, "ADD_PLAYER");

            // Create packets
            Constructor<?> packetConstructor = playerInfoPacketClass.getConstructor(playerInfoActionClass, Iterable.class);
            Object removePlayerPacket = packetConstructor.newInstance(removePlayerAction, Collections.singletonList(serverPlayer));
            Object addPlayerPacket = packetConstructor.newInstance(addPlayerAction, Collections.singletonList(serverPlayer));

            // Send packets to all online players
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                Object onlineServerPlayer = getHandleMethod.invoke(onlinePlayer);
                Object onlineConnection = connectionField.get(onlineServerPlayer);
                Method sendPacketMethod = connectionClass.getDeclaredMethod("send", packetClass);
                sendPacketMethod.invoke(onlineConnection, removePlayerPacket);
                sendPacketMethod.invoke(onlineConnection, addPlayerPacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getServerVersion() {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        return packageName.substring(packageName.lastIndexOf('.') + 1);

    }
}

