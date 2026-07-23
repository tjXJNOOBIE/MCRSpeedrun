package com.tjxjnoobie.speed.managers;

import com.tjxjnoobie.api.enums.GameStateEnum;
import com.tjxjnoobie.api.interfaces.IGameState;
import com.tjxjnoobie.api.interfaces.IRankCache;
import com.tjxjnoobie.api.interfaces.IVoting;
import org.tavall.dependency.annotations.PostConstruct;
import com.tjxjnoobie.api.platform.minecraft.utils.interfaces.IMCUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Voting implements IVoting, IMCUtils, IRankCache, IGameState {

    private boolean isComplete = false;

    public HashMap<String, Integer> voting = new HashMap<>();
    public ArrayList<UUID> voted = new ArrayList<>();
    public String winner;
    private Plugin plugin;



    @PostConstruct
    private void init(){
        loadGameModes();
        runVoting();
    }

    @Override
    public void loadGameModes(){
        voting.put("Normal",0);
        voting.put("Speed",0);
        voting.put("Elimination", 0);
        voting.put("Teams",0);
        voting.put("Random",0);

    }
    @Override
    public void addVote(String gamemode, int votes) {
        voting.put(gamemode, getVotes(gamemode) + votes);
    }
    @Override
    public Integer getVotes(String gamemode) {
        return voting.get(gamemode);
    }
    @Override
    public String getWinner() {
        return winner;
    }
    @Override
    public void vote(Player player, String gamemode) {
        UUID uuid = player.getUniqueId();

        if(isComplete){
            player.sendMessage(getMinecraftPrefix()+"§cVoting has already concluded!");
            return;
        }
        if(hasVoted(uuid)){
            player.sendMessage(getMinecraftPrefix()+"§cYou have already voted!");
            return;
        }
        
        if (getCachedRank(uuid).equals("Developer") || getCachedRank(uuid).equals("Owner")
                || getCachedRank(uuid).equals("HeadAdmin")) {
            addVote(gamemode, getVotes(gamemode) + 100);
        } else if (getCachedRank(uuid).equals("Mod") || getCachedRank(uuid).equals("SrMod")
                || getCachedRank(uuid).equals("Admin")) {
            addVote(gamemode, getVotes(gamemode) + 10);
        } else if (getCachedRank(uuid).equals("Partner")) {
            addVote(gamemode, getVotes(gamemode) + 10);
        } else if (getCachedRank(uuid).equals("Premier")) {
            addVote(gamemode, getVotes(gamemode) + 10);
        } else if (getCachedRank(uuid).equals("Prime")) {
            addVote(gamemode, getVotes(gamemode) + 5);
        } else if (getCachedRank(uuid).equals("Premium")) {
            addVote(gamemode, getVotes(gamemode) + 3);
        } else if (getCachedRank(uuid).equals("Supporter")) {
            addVote(gamemode, getVotes(gamemode) + 2);
        } else {
            addVote(gamemode, getVotes(gamemode) + 1);
        }
        System.out.println(uuid.toString()+" is a " + getCachedRank(uuid)+" voting");

    }

    @Override
    public boolean isComplete() {
        return isComplete;
    }
    @Override
    public boolean hasVoted(UUID uuid){
        return voted.contains(uuid);
    }
    @Override
    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    @Override
    public String getVotingOptions() {
        StringBuilder message = new StringBuilder("§c!! §eGamemode Voting §c!!\n");
        int index = 1;
        for (String gamemode : voting.keySet()) {
            message.append("§c"+index).append("§7.§e ").append(gamemode).append(" §8| §fVotes§7:§c ").append(voting.get(gamemode)).append("\n");
            index++;
        }
        return message.toString();
    }
    @Override
    public void sendVotingOptions(Player player) {
        player.sendMessage(getVotingOptions());
    }
        public void runVoting(){
        new BukkitRunnable(){
            @Override
            public void run() {
                GameStateEnum currentState = getCurrentState();
                Player aplayer = getAllMinecraftPlayers();
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
    @Override
    public HashMap<String, Integer> getGameModes(){
        return voting;
    }
    @Override
    public List<UUID> getHasVotedHash(){
        return voted;
    }
    @Override
    public void calculateAndAnnounceWinner() {


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

        String announcement = getMinecraftPrefix()+"§aVoting is complete! The winning gamemode is §c"
                + winningGamemode;

        // Announce to all online players
        Player aplayer = getAllMinecraftPlayers();
        aplayer.sendMessage(announcement);

        // Optionally, print to the console as well
        System.out.println("Voting complete. Winner: " + winningGamemode + " with " + highestVotes + " votes.");
    }
}