package com.tjxjnoobie.speed.managers;

import com.tjxjnoobie.api.abstracts.AbstractGameStateManager;
import com.tjxjnoobie.api.platform.global.annotations.Inject;
import com.tjxjnoobie.api.enums.GameModeEnum;
import com.tjxjnoobie.api.enums.GameStateEnum;
import com.tjxjnoobie.api.interfaces.*;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameManager extends AbstractGameStateManager<ISpeedRunContext> implements IGameManager, IWorldManager, IMCUtils {

    public HashMap<Long, Location> spawn = new HashMap<>();
    public HashMap<UUID, String> ingame = new HashMap<>();
    public HashMap<UUID, String> allPlayers = new HashMap<>();
    public HashMap<UUID, String> watching = new HashMap<>();
    public HashMap<UUID, String> in_nether = new HashMap<>();
    public HashMap<UUID, String> in_end = new HashMap<>();
    public HashMap<UUID, String> quit = new HashMap<>();
    public HashMap<UUID, String> blaze_rod = new HashMap<>();
    public HashMap<UUID, String> ender_eye = new HashMap<>();
    public HashMap<UUID, String> ender_pearl = new HashMap<>();
    public HashMap<UUID, String> killed_blaze = new HashMap<>();
    public HashMap<UUID, String> killed_enderdragon = new HashMap<>();
    public HashMap<UUID, Location> quit_location = new HashMap<>();
    public HashMap<UUID, Long> final_time = new HashMap<>();
    public ArrayList<Player> winner = new ArrayList<>();
    public ArrayList<UUID> finished_players = new ArrayList<>();



    public int minPlayers = 2;
    public int maxPlayers = 4;
    public int lobbyCountdown = 60;
    public int preGameCount = 15;
    public long timeElapsed = 0;
    public int soloTime = 0;
    public int millis = 0;
    public int incrementRate = 50;
    public int finished = 0;
    public long animatedMillis = 0;
    public long seed = -5584399987456711267L;
    public long startTime;
    public int online = Bukkit.getOnlinePlayers().size();
    public boolean canSolo = false;
    public boolean hasSeed = false;
    public boolean canMove = true;
    public GameStateEnum gameStateEnum;

    @Inject private SpeedRunContext speedRunContext;
    @Inject private IGlobalContext globalContext;
    @Inject private Plugin plugin; // Will be injected automatically from context
    private BukkitTask lobbyTimer;
    private BukkitTask lobby;
    private BukkitTask checkers;
    private BukkitTask lobbyChecker;
    private BukkitTask preLobbyChecker;
    public BukkitTask hotBarTimer;


    public GameManager() {
        super(GameStateEnum.LOBBY);
    }


      
    public long getSeed() {
        return seed;
    }

      
    public long getTimeElapsedLong() {
        return timeElapsed;
    }

      
    public int getMinPlayers() {
        return minPlayers;
    }

      
    public int getCurrentPlayers() {
        return ingame.size();
    }

      
    public long getFinalTime(UUID uuid){
        return final_time.get(uuid);
    }

      
    public String getFinalTimeString(UUID uuid){
        long finalTime = final_time.get(uuid);
        finalTime = System.currentTimeMillis() - startTime;

        // Convert to hours, minutes, seconds, and milliseconds
        long hours = finalTime / (1000 * 60 * 60);
        long minutes = (finalTime / (1000 * 60)) % 60;
        long seconds = (finalTime / 1000) % 60;
        long milliseconds = (finalTime % 1000) / 10; // Get 3/1000ths


        return String.format("§a%02d§8:§a%02d§8:§a%02d§8.§a%02d", hours, minutes, seconds, milliseconds);
    }
      
    public String getCurrentTime(){
        long finalTime = System.currentTimeMillis() - startTime;
        long hours = finalTime / (1000 * 60 * 60);
        long minutes = (finalTime / (1000 * 60)) % 60;
        long seconds = (finalTime / 1000) % 60;
        long milliseconds = (finalTime % 1000) / 10; // Get 3/1000ths

        // Format time
        return String.format("%02d:%02d:%02d.%02d", hours, minutes, seconds, milliseconds);
    }
      
    public long getCurrentTimeLong(){
        return System.currentTimeMillis() - startTime;
    }

      
    public UUID getPlayerUUID(String name) {
        Map.Entry<UUID, String> playeruuid = null;
        for (Map.Entry<UUID, String> entry : allPlayers.entrySet()) {
            if (entry.getValue().equals(name)) {
                playeruuid = allPlayers.entrySet().iterator().next();
                return playeruuid.getKey();

            }
        }
        return null;

    }


    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getPlayersRemaining() {
        return ingame.size();
    }

    public int getSpectatorsInt() {
        return watching.size();
    }

    public int getPlayersInNetherInt() {
        return in_nether.size();
    }

    public int getPlayersInEndInt() {
        return in_end.size();
    }


    public int getAllPlayersInt() {
        return allPlayers.size();
    }

    public int getPregameTime() {
        return preGameCount;
    }

    public int getLobbyCountDown() {
        return lobbyCountdown;
    }

    public int getPlayerNeeded(){
        return minPlayers;
    }
    public int getFinished() {
        return finished;
    }




    public Player getWinner(){
        return winner.getFirst();
    }


    public Player getInGamePlayers(){
        for(UUID playerUUID : ingame.keySet()){
            Player player = Bukkit.getPlayer(playerUUID);
            if(player != null && player.isOnline()){
                return player;
            }
        }
        return null;
    }
    public Player getPlayersWatching(){
        for(UUID playerUUID : watching.keySet()){
            Player player = Bukkit.getPlayer(playerUUID);
            if(player != null && player.isOnline()){
                return player;
            }
        }
        return null;
    }
    public Player getAllPlayers(){
        for(UUID playerUUID : allPlayers.keySet()){
            Player player = Bukkit.getPlayer(playerUUID);
            if(player != null && player.isOnline()){
                return player;
            }
        }
        return null;
    }


    public boolean isSpectator(UUID uuid) {
        return watching.containsKey(uuid);
    }
    public boolean hasBlazeRod(UUID uuid){
        return blaze_rod.containsKey(uuid);
    }
    public boolean hasEnderEye(UUID uuid){
        return ender_eye.containsKey(uuid);
    }
    public boolean hasEnderPearl(UUID uuid){
        return ender_eye.containsKey(uuid);
    }
    public boolean hasKilledBlaze(UUID uuid){
        return killed_blaze.containsKey(uuid);
    }
    public boolean canSolo(){
        return canSolo;
    }
    public boolean canMove(){
        return canMove;
    }

    public HashMap<UUID, String> getPlaying() {
        return ingame;
    }
    public HashMap<UUID, String> getWatching() {
        return watching;
    }
    public HashMap<UUID, String> getAllPlayersHash() {
        return allPlayers;
    }
    public HashMap<UUID, String> getBlazeRod() {
        return blaze_rod;
    }
    public HashMap<UUID, String> getEnderPearl() {
        return ender_pearl;
    }
    public HashMap<UUID, String> getEyeOfEnder() {
        return ender_eye;
    }
    public HashMap<UUID, String> getKilledBlaze() {
        return killed_blaze;
    }
    public HashMap<UUID, String> getEnderDragon() {
        return killed_enderdragon;
    }
    public HashMap<UUID, String> getInNetherHash() {
        return in_nether;
    }

    public HashMap<UUID, String> getFinishedPlayers() {
        return in_nether;
    }

    public HashMap<UUID, String> getInEnd() {
        return in_end;
    }


    public HashMap<UUID, String> getQuitPlayers() {
        return quit;
    }
    public HashMap<UUID, Location> QuitLocation() {
        return quit_location;
    }
    public String getTimeElapsed() {
        long hours = (timeElapsed / 3600000) % 24; // Convert to hours
        long minutes = (timeElapsed / 60000) % 60; // Convert to minutes
        long seconds = (timeElapsed / 1000) % 60;  // Convert to seconds
        long millis = timeElapsed % 1000;         // Get remaining milliseconds

        return String.format("%02d:%02d:%02d:%03d", hours, minutes, seconds, millis);
    }

    public Location getSpawnFromSeed(long seed) {
        return spawn.get(seed);
    }
    public Location getQuitLocation(UUID uuid) {
        return quit_location.get(uuid); // Returns null if the UUID is not in the map
    }

    public void addPlayer(UUID uuid, String name) {
        allPlayers.put(uuid, name);

    }

    public void addInGame(UUID uuid, String name) {
        ingame.put(uuid, name);

    }

    public void addWatching(UUID uuid, String name) {
        watching.put(uuid, name);

    }

    public void addInNether(UUID uuid, String name) {
        in_nether.put(uuid, name);

    }

    public void addInEnder(UUID uuid, String name) {
        in_end.put(uuid, name);

    }
    public void addBlazeRod(UUID uuid, String name) {
        blaze_rod.put(uuid, name);

    }
    public void addEnderEye(UUID uuid, String name) {
        ender_eye.put(uuid, name);

    }
    public void addEnderPearl(UUID uuid, String name) {
        ender_pearl.put(uuid, name);

    }
    public void addFinishedPlayer(UUID uuid) {
        finished_players.add(uuid);

    }
    public void addFinished() {
        finished = finished+1;
    }
    public void setWinner(Player player){
        winner.add(player);
    }
    public void setFinalTime(UUID uuid){
        long finalTime = System.currentTimeMillis() - startTime;
        final_time.put(uuid,finalTime);
    }




    public void runCheckers(){
        IGameState gameState = speedRunContext.getGameState();
        IGameMode gameMode = speedRunContext.getGameMode();
         
       checkers = new BukkitRunnable(){
              
            public void run() {
                GameStateEnum currentState = gameState.getCurrentState();
                int ingameSize = ingame.size();
                int soloMessage = 29;
                GameModeEnum currentGM = gameMode.getCurrentGameMode();
                Player player =   getAllPlayers();
                if(player == null){
                    return;
                }
                if(currentState== GameStateEnum.LOBBY) {
                    Location plocation = player.getLocation();

                    if (ingameSize == 1 && soloTime >= 120) {
                        soloMessage--;
                        if(soloMessage == 30)
                        canSolo = true;
                        Bukkit.broadcastMessage(getMinecraftStaffPrefix() + "You can play SOLO! Just type /solo");
                          playSoundForAll(plocation, Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.0f);
                        if(soloMessage == 0) {
                            soloMessage = 30;
                        }
                    } else if(ingameSize > 0){
                        canSolo = false;
                    }

                    if (ingameSize == minPlayers) {
                        Player allPlayers =   getAllPlayers();
                        Location pLocation = allPlayers.getLocation();
                        Bukkit.broadcastMessage(getMinecraftStaffPrefix() + "Minimum number of players reached! Starting match...");

                        lobbyCountdown = 11;
                        startLobbyCountdown();
                          playSoundForAll(pLocation, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                        cancel();
                    }

                }else if(currentState == GameStateEnum.PREGAME) {
                    if(ingame.isEmpty() && currentGM != GameModeEnum.SOLO) {
                        Bukkit.getLogger().info("All players left the server in pre lobby Gamestate: " + gameState + " ingame: " + ingameSize + " gamemode: " + gameMode);
                        //restart game or whatever
                    }
                }else if(currentState == GameStateEnum.INGAME) {


                }
            }
        }.runTaskTimer(plugin,100L,1L);
    }
    public void startLobby() {{
        IGameState gameState = speedRunContext.getGameState();
          
          
         
            new BukkitRunnable() {
                  
                public void run() {
                    Bukkit.broadcastMessage("Running Lobby Checker");

                    int ingame = getPlaying().size();
                    int startPlayers = minPlayers - getPlaying().size();
                    GameStateEnum currentState = gameState.getCurrentState();
                    if (!(currentState == GameStateEnum.LOBBY)) {
                        cancel();
                    }
                    if(ingame == 0){
                        runCheckers();
                        cancel();
                        Bukkit.getLogger().info("Server is empty returning to pre check status");
                    }
                    if(ingame==1){
                        soloTime++;
                    }

                    if (ingame < minPlayers && currentState == GameStateEnum.LOBBY) {
                        Player players =   getAllPlayers();
                        if(players == null){
                            return;
                        }
                        Location plocation = players.getLocation();
                        Bukkit.broadcastMessage(getMinecraftPrefix() + "§c" + startPlayers + " §fMore player(s) are needed to start ");
                          playSoundForAll(plocation, Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.0f);
                        players.playSound(plocation,Sound.ENTITY_PLAYER_LEVELUP,1.0f,1.0f);
                    }else{
                        cancel();
                        startLobbyCountdown();
                    }

                }
            }.runTaskTimer(plugin, 0L, 400L);
        }
    }

    public void startPreGame() throws SQLException {
        IGameState gameState = speedRunContext.getGameState();
          
        
        ISpeedRunJoinEvent joinEvent = speedRunContext.getJoinEvent();
        IBossBarManager bossBarManager = speedRunContext.getBossBarManager();
        new BukkitRunnable() {
              
            public void run() {
                teleportPlayersToWorlds();
            }
        }.runTaskLater(plugin, 20 * 5);

        Player aplayers = getAllPlayers();
        String serverID =   getServerID();
        gameState.setGameState(GameStateEnum.PREGAME, serverID);
        createWorlds(World.Environment.NORMAL);
        new BukkitRunnable() {
              
            public void run() {
                Player allPlayers = getAllPlayers();
                Location allPlayersLocation = getAllPlayers().getLocation();

                if (preGameCount == 0) {
                    cancel();
                    Bukkit.broadcastMessage(getMinecraftPrefix()+ "Match has started!");
                    playSoundForAll(allPlayersLocation, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);

                    // Final title update before match starts
                    allPlayers.sendTitle("§aMATCH STARTED!!!", "", 10, 40, 10);

                    try {
                        for(String bossBar : bossBarManager.getActiveBossBars()){
                            if(bossBar.contains(" ")){
                                bossBarManager.cleanup();
                            }
                        }
                        bossBarManager.removePlayer(allPlayers);
                        gameState.setGameState(GameStateEnum.INGAME,serverID);
                        startGame();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return;
                }

                if (preGameCount == 60 || preGameCount == 30 || (preGameCount <= 10 && preGameCount > 0)) {
                    Bukkit.broadcastMessage(getMinecraftPrefix()+ "Match starting in §c" + preGameCount + " §fseconds!");
                    playSoundForAll(allPlayersLocation, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);

                    // Send title only once at 10 seconds
                    if (preGameCount == 10) {
                        allPlayers.sendTitle("§eMatch Starting in...", "§b§l▶▶§c" + preGameCount + " §b§l◀◀", 10, 9999, 0);
                    } else {
                        // Only update the subtitle while keeping the title static
                        allPlayers.sendTitle("§eMatch Starting in...", "§b§l▶▶§c " + preGameCount + " §b§l◀◀", 0, 9999, 0);
                    }
                }

                preGameCount--;
            }
        }.runTaskTimer(plugin, 0, 20L);
    }





    public void startLobbyCountdown() {
        IVoting voting = speedRunContext.getVoting();
        
         
        lobbyTimer = new BukkitRunnable() {
              
            public void run() {
                Player aplayers =   getAllPlayers();
                Location aplocation = aplayers.getLocation();
                Bukkit.broadcastMessage("Running Lobby countdown");

                if (lobbyCountdown == 60 || lobbyCountdown == 30 || (lobbyCountdown <= 10 && lobbyCountdown > 0)) {
                    Bukkit.broadcastMessage(getMinecraftPrefix()+ lobbyCountdown + " seconds until the match starts!");
                      playSoundForAll(aplocation, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);

                    // Send title only once at 60 seconds
                    if (lobbyCountdown == 60) {
                        aplayers.sendTitle("§eMatch Starting in...", "§b§l▶▶§c " + lobbyCountdown + " §b§l◀◀", 10, 9999, 0);
                    } else {
                        // Only update subtitle, keeping the main title static
                        aplayers.sendTitle("§eMatch Starting in...", "§b§l▶▶§c " + lobbyCountdown + " §b§l◀◀", 0, 9999, 0);
                    }
                }

                if (lobbyCountdown == 0) {
                    voting.calculateAndAnnounceWinner();
                    cancel();
                    Bukkit.broadcastMessage(getMinecraftPrefix()+ "§cMatch is starting!");
                    canMove =false;
                      playSoundForAll(aplocation, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);

                    // Final title update
                    aplayers.sendTitle("§eMatch Starting...", "", 10, 40, 10);

                    try {
                        startPreGame();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }

                lobbyCountdown--;
            }
        }.runTaskTimer(plugin, 0, 20L);
    }


    public void startGame() throws SQLException {
        IGameState gameState = speedRunContext.getGameState();
        IGameMode gameMode = speedRunContext.getGameMode();
          
          
         
        startTime = System.currentTimeMillis();
        runHotBarTimer();
        runGame();
        int players = ingame.size();
        String serverID =    getServerID();
        GameModeEnum CurrentGM = gameMode.getCurrentGameMode() ;
        Player allPlayers =   getAllPlayers();
        playDramaticBoom(allPlayers);
        if(players == 0 && CurrentGM == GameModeEnum.SOLO){

        }
    }

    public void runGame() throws SQLException {
          
        String serverID =    getServerID();

    }


    public void stopGame() throws SQLException {
        IGameState gameState = speedRunContext.getGameState();
          
          
        IWorldManager worldManager = speedRunContext.getWorldManager();
         
        String serverID =    getServerID();
        Player aplayers =   getAllPlayers();
        World world = Bukkit.getWorld("lobby");
        Location spawn = world.getSpawnLocation();
        gameState.setGameState(GameStateEnum.ENDING,serverID);
        System.out.println("Starting cleanup...");
        hotBarTimer.cancel();
        // Teleport to spawn/peds
        new BukkitRunnable(){
              
            public void run() {
                aplayers.teleport(spawn);
            }
        }.runTaskLater(plugin,20*15);
        // Delete player worlds
        new BukkitRunnable(){
              
            public void run() {
                for(UUID ingame_uuid : ingame.keySet()) {

                    System.out.print("Deleting worlds from play " + ingame_uuid.toString());
                    worldManager.deleteWorld(ingame_uuid.toString()+"_NORMAL");
                    worldManager.deleteWorld(ingame_uuid.toString()+"_NETHER");
                    worldManager.deleteWorld(ingame_uuid.toString()+"_ENDER");

                }            }
        }.runTaskLater(plugin,20*20);

    }

    public void runHotBarTimer(){
         
        hotBarTimer = new BukkitRunnable() {
              
            public void run() {
                // Calculate elapsed time
                timeElapsed = System.currentTimeMillis() - startTime;

                // Convert to hours, minutes, seconds, and milliseconds
                long hours = timeElapsed / (1000 * 60 * 60);
                long minutes = (timeElapsed / (1000 * 60)) % 60;
                long seconds = (timeElapsed / 1000) % 60;
                long milliseconds = (timeElapsed % 1000) / 10; // Get 3/1000ths

                // Format time
                String formattedTime = String.format("§a%02d§8:§a%02d§8:§a%02d§8.§a%02d", hours, minutes, seconds, milliseconds);

                // Send to all players
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(formattedTime));
                }
            }
        }.runTaskTimer(plugin, 0L, 1L); // Repeat every tick (1L)
    }

    public void createWorlds(World.Environment environment){
        IWorldManager worldManager = speedRunContext.getWorldManager();
          
        Bukkit.broadcastMessage(getMinecraftStaffPrefix()+" §cLoading Worlds...");
            for (UUID ingame_uuid : ingame.keySet()) {
                Player player = Bukkit.getPlayer(ingame_uuid);
                System.out.print("Creating world " + ingame_uuid.toString());
                worldManager.createWorld(ingame_uuid.toString() + "_" + environment.name(), environment);
                worldManager.createWorld(ingame_uuid.toString()+"_"+environment.name(), World.Environment.NETHER);
            }
    }
    public void createWorldsWithSeed(World.Environment environment, Long seed){
          
        Bukkit.broadcastMessage(getMinecraftPrefix()+" §cLoading Worlds...");

        for(UUID ingame_uuid : ingame.keySet()) {
            IWorldManager worldManager = speedRunContext.getWorldManager();
            System.out.print("Creating world " + ingame_uuid.toString());
            worldManager.createWorldFromSeed(ingame_uuid.toString()+"_"+environment.name(),environment,seed);
        }
    }

    public void teleportPlayersToWorlds() {
        for (UUID ingame_uuid : ingame.keySet()) {
            Player player = Bukkit.getPlayer(ingame_uuid);
            String name = player.getName();
            String env = player.getWorld().getEnvironment().name();
            World world = Bukkit.getWorld(ingame_uuid + "_" + env);
            Location spawn = world.getSpawnLocation();
            player.teleport(spawn);
        }
    }
        //TODO: Update game loop to abstract
    @Override
    protected void onLobbyStart() {

    }

    @Override
    protected void onPreGameStart() {

    }

    @Override
    protected void onGameStart() {

    }

    @Override
    protected void onGameEnd() {

    }
}


