package com.tjxjnoobie.api.interfaces;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Interface for voting system operations
 */
public interface IVoting extends com.tjxjnoobie.api.dependency.IDependencyInjectableInterface {

    default void calculateAndAnnounceWinner() {
        // no-op
    }

    default boolean isComplete() {
        return false;
    }

    default boolean hasVoted(UUID uuid) {
        return false;
    }

    default void setComplete(boolean complete) {
        // no-op
    }

    default String getVotingOptions() {
        return "";
    }

    default void sendVotingOptions(Player player) {
        // no-op
    }

    default HashMap<String, Integer> getGameModes() {
        return new HashMap<>();
    }

    default void loadGameModes() {
        // no-op
    }

    default void addVote(String gamemode, int votes) {
        // no-op
    }

    default Integer getVotes(String gamemode) {
        return 0;
    }

    default String getWinner() {
        return "";
    }

    default void vote(Player player, String gamemode) {
        // no-op
    }

    default List<UUID> getHasVotedHash() {
        return List.of();
    }
}
