package com.tjxjnoobie.speed.Events;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.tjxjnoobie.API.GlobalContext;
import com.tjxjnoobie.API.cache.SpeedrunStatsCache;
import com.tjxjnoobie.API.managers.GameMode;
import com.tjxjnoobie.API.managers.StatsManager;
import com.tjxjnoobie.API.minecraft.utils.MCUtils;
import com.tjxjnoobie.API.minecraft.managers.SoundManager;
import com.tjxjnoobie.abstracts.AbstractManager;
import com.tjxjnoobie.enums.GameModeEnum;
import com.tjxjnoobie.interfaces.*;
import com.tjxjnoobie.speed.managers.BossBarManager;
import com.tjxjnoobie.speed.managers.GameManager;
import com.tjxjnoobie.API.utils.Utils;
import com.tjxjnoobie.speed.managers.PlayerManager;
import com.tjxjnoobie.speed.managers.SpeedRunContext;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
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

public abstract class MobKill extends AbstractManager<SpeedRunContext> implements Listener, IUtils {


    private final SpeedRunContext speedRunContext;
    boolean KilledBlaze = false;
    boolean killedEnderDragon = false;


    public MobKill(SpeedRunContext speedRunContext) {
        super(speedRunContext);

        this.speedRunContext = speedRunContext;
    }

    @Override
    protected void doInitialize() throws Exception {

    }

    @EventHandler
    public void onMobKill(EntityDeathEvent e) throws SQLException {
        IMCUtils mcUtils = speedRunContext.getMcUtils();
        IGameManager gameManager = speedRunContext.getGameManager();
        ISoundManager soundManager = speedRunContext.getSoundManager();
        IUtils utils = speedRunContext.getUtils();
        Plugin plugin = speedRunContext.getPlugin();
        IPlayerManager playerManager = speedRunContext.getPlayerManager();
        Player killer = e.getEntity().getKiller();
        UUID uuid = killer.getUniqueId();
        String name = killer.getName();
        String displayName = killer.getDisplayName();
        String finalTime = gameManager.getFinalTimeString(uuid);
        String currentTime = gameManager.getCurrentTime();
        long currentTimeLong = gameManager.getCurrentTimeLong();
        Player allPlayers = mcUtils.getAllPlayers();
        Location allLocation = allPlayers.getLocation();
        IGameMode gameMode = speedRunContext.getGameMode();
        GameModeEnum currentMode = gameMode.getCurrentGameMode();
        IStatsManager statsManager = speedRunContext.getStatsManager();
        ISpeedrunStatsCache srStatsCache = speedRunContext.getSRStatsCache();
        boolean playerKilledBlaze = gameManager.hasKilledBlaze(uuid);



        if (e.getEntityType() == EntityType.BLAZE && !KilledBlaze) {
            KilledBlaze = true;
            gameManager.getKilledBlaze().put(uuid, name);
            Bukkit.broadcastMessage(displayName + " was the first to kill a §cBlaze!");
        } else if (KilledBlaze && e.getEntityType() == EntityType.BLAZE) {
            gameManager.getKilledBlaze().put(uuid, name);
            Bukkit.broadcastMessage(displayName + " killed a §cBlaze! §7(" +currentTime + ")");
        }

        if (e.getEntity() instanceof EnderDragon && !killedEnderDragon) {
            if(gameManager.getFinished() == gameManager.getPlaying().size()){
                return;
            }
            if(currentMode== GameModeEnum.SOLO){
            srStatsCache.addBestTime(uuid,finalTime);
            killer.sendMessage(utils.prefix+"You have killed the §cEnder Dragon§f!");
            killer.sendMessage(utils.prefix+"Final Time: " + finalTime);
            gameManager.stopGame();
            }
            soundManager.playVictoryWithDragonDeath(killer);
            gameManager.setFinalTime(uuid);
            killedEnderDragon = true;
            Bukkit.broadcastMessage(getStaffPre+ displayName + " §b§lHAS KILLED THE §c§lENDER DRAGON§b§l!");
            Bukkit.broadcastMessage(utils.prefix + displayName + "'s Final Time: " + finalTime);
            gameManager.addFinished();
            gameManager.addFinishedPlayer(uuid);
            gameManager.setWinner(killer);
            gameManager.getWinner().sendMessage(utils.prefix+"§a§lCongratulations! §cYou have won!");
            sendEnderDragonBossBarMessage(allPlayers,20*7);

        }else if(killedEnderDragon){
            int finished = gameManager.getFinished();
            int inGame = gameManager.getPlaying().size();
            soundManager.playVictoryWithDragonDeath(killer);
            gameManager.addFinished();
            gameManager.addFinishedPlayer(uuid);
            gameManager.setFinalTime(uuid);
            srStatsCache.addBestTime(uuid,finalTime);
            killer.sendMessage(utils.prefix+"Your final time is " + gameManager.getFinalTimeString(uuid));
            Bukkit.broadcastMessage(utils.prefix+displayName+" has killed the §cEnder Dragon"+ "§7(" +currentTime + ")");
            new BukkitRunnable(){
                @Override
                public void run() {
                    playerManager.makeSpectator(uuid,name,killer);
                }
            }.runTaskLater(plugin,20*5);
            sendEnderDragonBossBarMessage(allPlayers,20*7);
            if(finished == inGame){
                Bukkit.getLogger().info("All players have finished! Running end game");
                mcUtils.sendDebugMessage(allPlayers, getStaffPrefix()+"All players have finished! Running end game");
                gameManager.stopGame();
            }
        }
    }

    public void sendEnderDragonBossBarMessage(Player player, int time){
        IUtils utils = speedRunContext.getUtils();
        Plugin plugin = speedRunContext.getPlugin();
        String displayName = player.getDisplayName();

        BossBarManager bossBarManager = new BossBarManager(getStaffPrefix() + displayName + " §b§lHAS KILLED THE ENDERDRAGON!", BarColor.RED, BarStyle.SOLID);
        bossBarManager.addPlayer(player);
        new BukkitRunnable(){
            @Override
            public void run() {
                if(player == null){
                    Bukkit.getLogger().info("Player has left before bossbar manager could remove them");
                    return;
                }
                bossBarManager.removePlayer(player);
            }
        }.runTaskLater(plugin,time);
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
    public void calculateBestTime(UUID uuid, Player player){
        IGameManager gameManager = speedRunContext.getGameManager();
        SpeedrunStatsCache srStatsCache = g.getSpeedrunStatsCache();
        long bestTime = srStatsCache.getBestTimeLong(uuid);
        long playerFinalTime = gameManager.getFinalTime(uuid);
        if(playerFinalTime > bestTime){
            srStatsCache.setBestTimeLong(uuid,playerFinalTime);
            player.sendMessage("New personal best!");
        }
    }

    @Override
    public GlobalContext getGlobalContext() {
        return null;
    }
}





