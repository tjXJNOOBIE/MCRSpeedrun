package com.tjxjnoobie.speed.managers;

import com.tjxjnoobie.API.cache.RankCache;
import com.tjxjnoobie.API.managers.GameState;
import com.tjxjnoobie.API.minecraft.utils.MCUtils;
import com.tjxjnoobie.API.utils.Utils;
import com.tjxjnoobie.API.velocity.Rank;
import com.tjxjnoobie.enums.GameModeEnum;
import com.tjxjnoobie.enums.GameStateEnum;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.web3j.crypto.Hash;

import java.sql.SQLException;
import java.util.*;

public class Voting {

    private boolean isComplete = false;

    public HashMap<String, Integer> voting = new HashMap<>();
    public ArrayList<UUID> voted = new ArrayList<>();
    public String winner;
    private final SpeedRunContext speedRunContext;
    private final Plugin plugin;


    public Voting(SpeedRunContext speedRunContext, Plugin plugin) {
        this.speedRunContext = speedRunContext;
        this.plugin = plugin;
        loadGameModes();
        runVoting();
    }


    public void loadGameModes(){
        voting.put("Normal",0);
        voting.put("Speed",0);
        voting.put("Elimination", 0);
        voting.put("Teams",0);
        voting.put("Random",0);

    }
    public void addVote(String gamemode, int votes) {
        voting.put(gamemode, getVotes(gamemode) + votes);
    }

    public Integer getVotes(String gamemode) {
        return voting.get(gamemode);
    }

    public String getWinner() {
        return winner;
    }

    public void vote(Player player, String gamemode) throws SQLException {
        UUID uuid = player.getUniqueId();
        Utils utils = speedRunContext.getUtils();
        if(isComplete){
            player.sendMessage(utils.prefix+"§cVoting has already concluded!");
            return;
        }
        if(hasVoted(uuid)){
            player.sendMessage(utils.prefix+"§cYou have already voted!");
            return;
        }
        RankCache rankCache = speedRunContext.getRankCache();
        if (rankCache.getRank(uuid).equals("Developer") || rankCache.getRank(uuid).equals("Owner")
                || rankCache.getRank(uuid).equals("HeadAdmin")) {
            addVote(gamemode, getVotes(gamemode) + 100);
        } else if (rankCache.getRank(uuid).equals("Mod") || rankCache.getRank(uuid).equals("SrMod")
                || rankCache.getRank(uuid).equals("Admin")) {
            addVote(gamemode, getVotes(gamemode) + 10);
        } else if (rankCache.getRank(uuid).equals("Partner")) {
            addVote(gamemode, getVotes(gamemode) + 10);
        } else if (rankCache.getRank(uuid).equals("Premier")) {
            addVote(gamemode, getVotes(gamemode) + 10);
        } else if (rankCache.getRank(uuid).equals("Prime")) {
            addVote(gamemode, getVotes(gamemode) + 5);
        } else if (rankCache.getRank(uuid).equals("Premium")) {
            addVote(gamemode, getVotes(gamemode) + 3);
        } else if (rankCache.getRank(uuid).equals("Supporter")) {
            addVote(gamemode, getVotes(gamemode) + 2);
        } else {
            addVote(gamemode, getVotes(gamemode) + 1);
        }
        System.out.println(uuid.toString()+" is a " +rankCache.getRank(uuid)+" voting");

    }


    public boolean isComplete() {
        return isComplete;
    }
    public boolean hasVoted(UUID uuid){
        return voted.contains(uuid);
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }


    public String getVotingOptions() {
        StringBuilder message = new StringBuilder("§c!! §eGamemode Voting §c!!\n");
        int index = 1;
        for (String gamemode : voting.keySet()) {
            message.append("§c"+index).append("§7.§e ").append(gamemode).append(" §8| §fVotes§7:§c ").append(voting.get(gamemode)).append("\n");
            index++;
        }
        return message.toString();
    }
    public void sendVotingOptions(Player player) {
        player.sendMessage(getVotingOptions());
    }

        public void runVoting(){
        MCUtils mcUtils = speedRunContext.getMcUtils();
        new BukkitRunnable(){
            @Override
            public void run() {
                GameState gameState = speedRunContext.getGameState();
                GameStateEnum currentState = gameState.getCurrentState();
                Player aplayer = mcUtils.getAllPlayers();
                if (aplayer == null) {
                    return;
                }
                if (isComplete) {
                    cancel();
                }
                if (currentState == GameStateEnum.LOBBY) {
                    sendVotingOptions(aplayer);
                }else{
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 20*15);
    }

    public HashMap<String, Integer> getGameModes(){
        return voting;
    }

    public List<UUID> getHasVotedHash(){
        return voted;
    }
    public void calculateAndAnnounceWinner() {
        MCUtils mcUtils = speedRunContext.getMcUtils();
        Utils utils = speedRunContext.getUtils();

        int highestVotes = -1;
        String winningGamemode = "";

        for (Map.Entry<String, Integer> entry : voting.entrySet()) {
            if (entry.getValue() > highestVotes) {
                highestVotes = entry.getValue();
                winningGamemode = entry.getKey();
            }
        }

        this.winner = winningGamemode;
        setComplete(true);

        String announcement = utils.prefix+"§aVoting is complete! The winning gamemode is §c"
                + winningGamemode;

        // Announce to all online players
        Player aplayer = mcUtils.getAllPlayers();
        aplayer.sendMessage(announcement);

        // Optionally, print to the console as well
        System.out.println("Voting complete. Winner: " + winningGamemode + " with " + highestVotes + " votes.");
    }
}
