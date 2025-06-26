package com.tjxjnoobie.interfaces;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Interface for fair fight system
 */
public interface IFairFight {
    
    /**
     * Handles damage event for fair fight
     * @param event The damage event
     */
    void onEntityDamageByEntity(EntityDamageByEntityEvent event);
    
    /**
     * Checks if PvP is allowed between players
     * @param attacker The attacking player
     * @param victim The victim player
     * @return true if PvP is allowed
     */
    boolean isPvPAllowed(Player attacker, Player victim);
    
    /**
     * Processes fair fight logic
     * @param attacker The attacking player
     * @param victim The victim player
     * @param damage The damage amount
     * @return Modified damage amount
     */
    double processFairFight(Player attacker, Player victim, double damage);
    
    /**
     * Applies fair fight modifiers
     * @param attacker The attacking player
     * @param victim The victim player
     * @param baseDamage The base damage
     * @return Modified damage
     */
    double applyFairFightModifiers(Player attacker, Player victim, double baseDamage);
    
    /**
     * Checks if players are in fair fight range
     * @param player1 First player
     * @param player2 Second player
     * @return true if in fair fight range
     */
    boolean isInFairFightRange(Player player1, Player player2);
    
    /**
     * Gets fair fight status between players
     * @param player1 First player
     * @param player2 Second player
     * @return Fair fight status object
     */
    Object getFairFightStatus(Player player1, Player player2);
    
    /**
     * Enables fair fight for a player
     * @param player The player to enable fair fight for
     */
    void enableFairFight(Player player);
    
    /**
     * Disables fair fight for a player
     * @param player The player to disable fair fight for
     */
    void disableFairFight(Player player);
    
    /**
     * Checks if fair fight is enabled for a player
     * @param player The player to check
     * @return true if fair fight is enabled
     */
    boolean isFairFightEnabled(Player player);
    
    /**
     * Sets fair fight mode
     * @param mode The fair fight mode
     */
    void setFairFightMode(String mode);
    
    /**
     * Gets current fair fight mode
     * @return The fair fight mode
     */
    String getFairFightMode();
    
    /**
     * Calculates equipment balance
     * @param player1 First player
     * @param player2 Second player
     * @return Balance factor (1.0 = balanced, >1.0 = player1 advantage)
     */
    double calculateEquipmentBalance(Player player1, Player player2);
    
    /**
     * Applies balance corrections
     * @param attacker The attacking player
     * @param victim The victim player
     */
    void applyBalanceCorrections(Player attacker, Player victim);
    
    /**
     * Records fair fight statistics
     * @param attacker The attacking player
     * @param victim The victim player
     * @param damage The damage dealt
     */
    void recordFairFightStats(Player attacker, Player victim, double damage);
    
    /**
     * Gets fair fight statistics
     * @param player The player to get stats for
     * @return Fair fight statistics
     */
    Object getFairFightStats(Player player);
    
    /**
     * Resets fair fight data for a player
     * @param player The player to reset data for
     */
    void resetFairFightData(Player player);
    
    /**
     * Handles fair fight violation
     * @param player The violating player
     * @param violation The violation type
     */
    void handleFairFightViolation(Player player, String violation);
    
    /**
     * Checks if damage should be blocked
     * @param attacker The attacking player
     * @param victim The victim player
     * @return true if damage should be blocked
     */
    boolean shouldBlockDamage(Player attacker, Player victim);
    
    /**
     * Gets fair fight configuration
     * @return Configuration object
     */
    Object getFairFightConfig();
    
    /**
     * Updates fair fight configuration
     * @param config The new configuration
     */
    void updateFairFightConfig(Object config);
}