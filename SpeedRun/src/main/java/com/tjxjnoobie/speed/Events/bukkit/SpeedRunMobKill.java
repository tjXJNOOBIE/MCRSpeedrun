package com.tjxjnoobie.speed.Events.bukkit;


import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.tjxjnoobie.api.platform.global.annotations.Inject;
import com.tjxjnoobie.api.enums.GameModeEnum;
import com.tjxjnoobie.api.interfaces.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.UUID;

public class SpeedRunMobKill implements Listener, IMCUtils {


    @Inject
    private ISpeedRunContext speedRunContext;
    @Inject
    private IGlobalContext globalContext;
    boolean KilledBlaze = false;
    boolean killedEnderDragon = false;
    @Inject
    private IBossBarManager bossBarManager;
    @Inject
    private IMCUtils mcUtils;
    @Inject
    private IGameManager gameManager;
    @Inject
    private ISoundManager soundManager;
    @Inject
    private Plugin plugin;
    @Inject
    private IPlayerManager playerManager;
    @Inject
    private IGameMode gameMode;
    @Inject
    private IStatsManager statsManager;
    @Inject
    private ISpeedrunStatsCache srStatsCache;


    @EventHandler
    public void onMobKill(EntityDeathEvent e) throws SQLException {

        Player killer = e.getEntity().getKiller();
        UUID uuid = killer.getUniqueId();
        String name = killer.getName();
        String displayName = killer.getDisplayName();
        String finalTime = gameManager.getFinalTimeString(uuid);
        String currentTime = gameManager.getCurrentTime();
        long currentTimeLong = gameManager.getCurrentTimeLong();
        Player allPlayers = getAllPlayers();
        Location allLocation = allPlayers.getLocation();
        GameModeEnum currentMode = gameMode.getCurrentGameMode();

        boolean playerKilledBlaze = gameManager.hasKilledBlaze(uuid);


        if (e.getEntityType() == EntityType.BLAZE && !KilledBlaze) {
            KilledBlaze = true;
            gameManager.getKilledBlaze().put(uuid, name);
            Bukkit.broadcastMessage(displayName + " was the first to kill a §cBlaze!");
        } else if (KilledBlaze && e.getEntityType() == EntityType.BLAZE) {
            gameManager.getKilledBlaze().put(uuid, name);
            Bukkit.broadcastMessage(displayName + " killed a §cBlaze! §7(" + currentTime + ")");
        }

        if (e.getEntity() instanceof EnderDragon && !killedEnderDragon) {
            if (gameManager.getFinished() == gameManager.getPlaying().size()) {
                return;
            }
            if (currentMode == GameModeEnum.SOLO) {
                srStatsCache.addBestTime(uuid, finalTime);
                killer.sendMessage(getMinecraftPrefix() + "You have killed the §cEnder Dragon§f!");
                killer.sendMessage(getMinecraftPrefix() + "Final Time: " + finalTime);
                gameManager.stopGame();
            }
            soundManager.playVictoryWithDragonDeath(killer);
            gameManager.setFinalTime(uuid);
            killedEnderDragon = true;
            Bukkit.broadcastMessage(getMinecraftPrefix() + displayName + " §b§lHAS KILLED THE §c§lENDER DRAGON§b§l!");
            Bukkit.broadcastMessage(getMinecraftPrefix() + displayName + "'s Final Time: " + finalTime);
            gameManager.addFinished();
            gameManager.addFinishedPlayer(uuid);
            gameManager.setWinner(killer);
            gameManager.getWinner().sendMessage(getMinecraftPrefix() + "§a§lCongratulations! §cYou have won!");
            sendEnderDragonBossBarMessage(allPlayers, 20 * 7);

        } else if (killedEnderDragon) {
            int finished = gameManager.getFinished();
            int inGame = gameManager.getPlaying().size();
            soundManager.playVictoryWithDragonDeath(killer);
            gameManager.addFinished();
            gameManager.addFinishedPlayer(uuid);
            gameManager.setFinalTime(uuid);
            srStatsCache.addBestTime(uuid, finalTime);
            killer.sendMessage(getMinecraftPrefix() + "Your final time is " + gameManager.getFinalTimeString(uuid));
            Bukkit.broadcastMessage(getMinecraftPrefix() + displayName + " has killed the §cEnder Dragon" + "§7(" + currentTime + ")");
            new BukkitRunnable() {
                @Override
                public void run() {
                    playerManager.makeSpectator(uuid, name, killer);
                }
            }.runTaskLater(plugin, 20 * 5);
            sendEnderDragonBossBarMessage(allPlayers, 20 * 7);
            if (finished == inGame) {
                Bukkit.getLogger().info("All players have finished! Running end game");
                // TODO Update IMCUtils methods to remove contexts. mcUtils.sendDebugMessage(globalContext,allPlayers, getStaffPrefix()+"All players have finished! Running end game");
                gameManager.stopGame();
            }
        }
    }

    public void sendEnderDragonBossBarMessage(Player player, int time) {

        String displayName = player.getDisplayName();
        bossBarManager.addPlayer(player);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player == null) {
                    Bukkit.getLogger().info("Player has left before bossbar manager could remove them");
                    return;
                }
                bossBarManager.removePlayer(player);
            }
        }.runTaskLater(plugin, time);
    }

    public static void blockDragonDeathSound(JavaPlugin plugin) {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

        protocolManager.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.WORLD_EVENT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                // Read the effect ID from the packet (first integer field)
                int effectId = event.getPacket().getIntegers().read(0);

                // 1028 is typically used for the Ender Dragon death sound effect
                if (effectId == 1028) {
                    event.setCancelled(true);
                }
            }
        });
    }

    public void calculateBestTime(UUID uuid, Player player) {
        long bestTime = srStatsCache.getBestTimeLong(uuid);
        long playerFinalTime = gameManager.getFinalTime(uuid);
        if (playerFinalTime > bestTime) {
            srStatsCache.setBestTimeLong(uuid, playerFinalTime);
            player.sendMessage("New personal best!");
        }
    }
}







