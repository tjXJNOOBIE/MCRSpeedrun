package com.tjxjnoobie.interfaces;

import com.tjxjnoobie.API.managers.GameMode;
import com.tjxjnoobie.enums.GameModeEnum;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

/**
 * Interface for voting system operations
 */
public interface IVoting {
    
    /**
     * Starts a new vote
     * @param question The voting question
     * @param options Array of voting options
     * @param durationSeconds Duration of the vote in seconds
     * @return The vote ID
     */
    String startVote(String question, String[] options, int durationSeconds);
    
    /**
     * Casts a vote
     * @param voteId The vote ID
     * @param playerId The player's UUID
     * @param option The selected option
     * @return true if vote was cast successfully
     */
    boolean castVote(String voteId, UUID playerId, String option);
    
    /**
     * Casts a vote by option index
     * @param voteId The vote ID
     * @param playerId The player's UUID
     * @param optionIndex The option index
     * @return true if vote was cast successfully
     */
    boolean castVote(String voteId, UUID playerId, int optionIndex);
    
    /**
     * Ends a vote early
     * @param voteId The vote ID
     * @return The vote results
     */
    Object endVote(String voteId);
    
    /**
     * Gets current vote results
     * @param voteId The vote ID
     * @return The current results
     */
    Object getVoteResults(String voteId);
    
    /**
     * Gets the winning option
     * @param voteId The vote ID
     * @return The winning option or null if tie/no votes
     */
    String getWinningOption(String voteId);
    
    /**
     * Checks if a player has voted
     * @param voteId The vote ID
     * @param playerId The player's UUID
     * @return true if player has voted
     */
    boolean hasVoted(String voteId, UUID playerId);
    
    /**
     * Gets a player's vote
     * @param voteId The vote ID
     * @param playerId The player's UUID
     * @return The player's vote option or null if not voted
     */
    String getPlayerVote(String voteId, UUID playerId);
    
    /**
     * Checks if a vote is active
     * @param voteId The vote ID
     * @return true if vote is active
     */
    boolean isVoteActive(String voteId);
    
    /**
     * Gets all active votes
     * @return Array of active vote IDs
     */
    String[] getActiveVotes();
    
    /**
     * Gets vote information
     * @param voteId The vote ID
     * @return Vote information object
     */
    Object getVoteInfo(String voteId);
    
    /**
     * Sends vote status to a player
     * @param voteId The vote ID
     * @param player The player to send status to
     */
    void sendVoteStatus(String voteId, Player player);
    
    /**
     * Sends vote status to all players
     * @param voteId The vote ID
     */
    void sendVoteStatusToAll(String voteId);
    
    /**
     * Calculates and announces the winner
     * @param voteId The vote ID
     */
    void calculateAndAnnounceWinner(String voteId);
    
    /**
     * Calculates and announces the winner for the current vote
     */
    void calculateAndAnnounceWinner();
    
    /**
     * Removes a vote
     * @param voteId The vote ID
     */
    void removeVote(String voteId);
    
    /**
     * Clears all votes
     */
    void clearAllVotes();
    
    /**
     * Gets total vote count
     * @param voteId The vote ID
     * @return Total number of votes cast
     */
    int getTotalVotes(String voteId);
    
    /**
     * Gets vote count for an option
     * @param voteId The vote ID
     * @param option The option to count
     * @return Number of votes for the option
     */
    int getOptionVotes(String voteId, String option);
    
    /**
     * Sets vote reminder interval
     * @param voteId The vote ID
     * @param intervalSeconds Reminder interval in seconds
     */
    void setReminderInterval(String voteId, int intervalSeconds);

    HashMap<String, Integer> getGameModes();

}