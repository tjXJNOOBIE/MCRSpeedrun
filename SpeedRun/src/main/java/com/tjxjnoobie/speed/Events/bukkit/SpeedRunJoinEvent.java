package com.tjxjnoobie.speed.Events.bukkit;

import com.tjxjnoobie.api.platform.global.annotations.Inject;
import com.tjxjnoobie.api.platform.global.annotations.PostConstruct;
import com.tjxjnoobie.api.enums.GameStateEnum;
import com.tjxjnoobie.api.enums.GameTypeEnum;
import com.tjxjnoobie.api.interfaces.*;
import com.tjxjnoobie.api.platform.cache.RatingCache;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.sql.SQLException;
import java.util.UUID;

public class SpeedRunJoinEvent implements Listener, IBossBarManager, IGameType, ILocationCache, IGameState, IGameManager, IRatingCache {

    public BukkitTask bossBar;
    
    
    @PostConstruct
    private void init() {
        createBossBar("Waiting for players..." + " has joined", BarColor.BLUE, BarStyle.SOLID);

    }




    @EventHandler
    public void onJoin(PlayerJoinEvent e) throws SQLException {
        GameTypeEnum currentType = getGameType();

        if (currentType != GameTypeEnum.SPEED_RUN) {
            Bukkit.getLogger().info("Speedrun join event: GameType is not SPEEDRUN!");
            return;
        }

;
        GameStateEnum currentGameState = getCurrentState();
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        String name = player.getName();
        String displayName = player.getDisplayName();
        int inGamePlayers = Bukkit.getOnlinePlayers().size();
        int minPlayers = getMinPlayers();
        int maxPlayers = getMaxPlayers();
        int startPlayers = minPlayers - getPlaying().size();
        int playerCount = getPlayersRemaining();
        double progress = Math.min((double) inGamePlayers / minPlayers, 1.0);
        Location quitLocation = getQuitLocation(uuid);
        Location spawn = locationCache.getSpawn();
        boolean isQuitPlayer = getQuitPlayers().containsKey(uuid);

        ratingCache = new RatingCache(ratingAPI, uuid, 0.0, 0.0, 0.0);
        loadSpeedRunRatings(uuid);
        Bukkit.getLogger().info(name + " Logged in with Rating: " + getRating() + " Deviation: " + getDeviation() + " Vol: " + getVolatility());

        mcUtils.cancelBukkitTask(bossBar);
        addPlayer(player); // Add player to the boss bar

        if (isQuitPlayer && currentGameState != GameStateEnum.LOBBY && currentGameState != GameStateEnum.ENDING) {
            e.setJoinMessage(displayName + ChatColor.DARK_GRAY + " rejoined");
            getQuitPlayers().remove(uuid);
            getPlaying().put(uuid, name);
            player.teleport(quitLocation);
        } else if (currentGameState == GameStateEnum.LOBBY) {
            player.teleport(spawn);
            addPlayer(uuid, name);
            addInGame(uuid, name);
            statsCache.createStorage(uuid);
            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
            player.getInventory().clear();
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setExp(0);
            player.setLevel(0);
            player.setGameMode(GameMode.SURVIVAL);
            inventoryManager.openVotingInventory(player);
            setLobbyInventory(player);
            e.setJoinMessage(displayName + ChatColor.DARK_GRAY + " joined  §7(§c" + playerCount + "§7/§c" + maxPlayers + "§7)");
        } else if (currentGameState == GameStateEnum.INGAME) {
            addPlayer(uuid, name);
            addWatching(uuid, name);
            playerManager.makeSpectator(uuid, name, player);
        }

        String message = "§b§l▶▶ §c" + startPlayers + "§e Players needed to start.. §b§l◀◀";
        updateTitle(message);
        updateProgress(progress);

        startBossBarTask(player, currentGameState, plugin);
    }


    /**
     * Starts a repeating task to update the boss bar for a player based on the current game state.
     * The task cycles through different display states (WAITING, PLAYERS_NEEDED, GAME_STARTING)
     * and updates the boss bar title and progress accordingly. The task runs every tick and
     * adjusts the display based on the number of players and game state transitions.
     *
     * @param player The player for whom the boss bar is being updated.
     * @param currentGameState The current state of the game.
     * @param plugin The plugin instance used to schedule the task.
     */
    private void startBossBarTask(Player player, GameStateEnum currentGameState, Plugin plugin) {
        bossBar = new BukkitRunnable() {
            private enum DisplayState {
                WAITING,
                PLAYERS_NEEDED,
                GAME_STARTING
            }

            private DisplayState currentState = DisplayState.WAITING;
            private int dotCount = 0;
            private int tickCounter = 0;
            private int colorIndex = 0;
            private int arrowColorIndex = 0;

            @Override
            public void run() {
                GameStateEnum gameState = currentGameState;

                if (gameState != GameStateEnum.LOBBY) {
                      removePlayer(player);
                    cancel();
                    return;
                }

                tickCounter++;

                switch (currentState) {
                    case WAITING:
                        updateWaitingState();
                        break;

                    case PLAYERS_NEEDED:
                        updatePlayersNeededState();
                        break;

                    case GAME_STARTING:
                        updateGameStartingState();
                        break;
                }
            }

            private void updateWaitingState() {
                // Increment dotCount every 20 ticks (1 second)
                if (tickCounter % 20 == 0) {
                    dotCount = (dotCount + 1) % 5; // Cycle through 0 to 4
                    String dots = ".".repeat(dotCount);
                      updateTitle("§b§l▶▶ §e Waiting for players" + dots + " §b§l◀◀");

                    if (dotCount == 4) {
                        currentState = DisplayState.PLAYERS_NEEDED;
                    }
                }
            }

            private void updatePlayersNeededState() {
                int minPlayers = getMinPlayers();
                int currentPlayers = getCurrentPlayers();
                int playersNeeded = minPlayers - currentPlayers;
                double progress = Math.min((double) currentPlayers / minPlayers, 1.0);

                // Increment dotCount every 20 ticks (1 second)
                if (tickCounter % 20 == 0) {
                    dotCount = (dotCount + 1) % 5; // Cycle through 0 to 4
                    String dots = ".".repeat(dotCount);
                      updateProgress(progress);
                      updateTitle("§b§l▶▶ §c" + playersNeeded + "§e Players needed to start" + dots + " §b§l◀◀");

                    if (playersNeeded <= 0) {
                        currentState = DisplayState.GAME_STARTING;
                        tickCounter = 0; // Reset tick counter for GAME_STARTING state
                    } else if (dotCount == 4) {
                        currentState = DisplayState.WAITING;
                    }
                }
            }

            private void updateGameStartingState() {
                final ChatColor[] colors = {
                        ChatColor.BOLD, ChatColor.RED, ChatColor.GOLD, ChatColor.YELLOW, ChatColor.GREEN,
                        ChatColor.AQUA,ChatColor.BLUE, ChatColor.LIGHT_PURPLE
                };
                final ChatColor[] arrowColors = {
                        ChatColor.YELLOW, ChatColor.GOLD, ChatColor.AQUA, ChatColor.DARK_PURPLE
                };
                // Update the rainbow text every 3 ticks
                if (tickCounter % 2 == 0) {
                    colorIndex = (colorIndex + 1) % colors.length;
                }

                // Update the arrow colors every 10 ticks
                if (tickCounter % 7 == 0) {
                    arrowColorIndex = (arrowColorIndex + 1) % arrowColors.length;
                }

                String message = "STARTING GAME!!!!";
                StringBuilder rainbowMessage = new StringBuilder("§l");
                for (int i = 0; i < message.length(); i++) {
                    ChatColor color = colors[(colorIndex + i) % colors.length];
                    rainbowMessage.append(color).append(message.charAt(i));
                }

                ChatColor currentArrowColor = arrowColors[arrowColorIndex];
                String bossBarMessage = currentArrowColor + "▶▶ " + rainbowMessage + " " + currentArrowColor + "◀◀";

                  updateTitle(bossBarMessage);
            }
        }.runTaskTimer(plugin, 0L, 1L); // Schedule to run every tick
    }



    private void setLobbyInventory(Player player){
        // Voting Paper
        ItemStack votingPaper = new ItemStack(Material.PAPER);
        ItemMeta meta = votingPaper.getItemMeta();
        meta.setDisplayName("§aVote for a gamemode!");
        votingPaper.setItemMeta(meta);
        player.getInventory().setItem(8, votingPaper);
        // GameMode Selector
    }


}
