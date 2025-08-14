package com.tjxjnoobie.api.interfaces;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Interface for voting system operations
 */
public interface IVoting {
    

    void calculateAndAnnounceWinner();


    boolean isComplete();

    boolean hasVoted(UUID uuid);

    void setComplete(boolean complete);

    String getVotingOptions();

    void sendVotingOptions(Player player);

    HashMap<String, Integer> getGameModes();


    void loadGameModes();

    void addVote(String gamemode, int votes);

    Integer getVotes(String gamemode);

    String getWinner();

    void vote(Player player, String gamemode);

    List<UUID> getHasVotedHash();
}