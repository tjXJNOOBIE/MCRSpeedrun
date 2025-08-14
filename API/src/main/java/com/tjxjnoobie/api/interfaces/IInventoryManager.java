package com.tjxjnoobie.api.interfaces;


import com.tjxjnoobie.api.builders.InventoryBuilder;
import com.tjxjnoobie.api.enums.InventoryType;
import com.tjxjnoobie.api.platform.minecraft.inventory.InventoryHistory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Set;

/**
 * Interface for InventoryManager to provide inventory management capabilities
 */
public interface IInventoryManager  {
    
    // Core inventory operations

    void setPlayerInventoryType(Player player, InventoryType type, String title, int size, int page);
    void setPlayerInventoryType(Player player, InventoryType type);

    void addToHistory(Player player, InventoryType type, String title, int size, int page);

    InventoryType getPlayerInventoryType(Player player);
    void removePlayerInventoryType(Player player);
    void clearPlayerData(Player player);
    boolean hasInventoryType(Player player, InventoryType type);
    
    // History management
    InventoryHistory getLastInventory(Player player);
    List<InventoryHistory> getInventoryHistory(Player player);
    boolean openLastInventory(Player player);
    
    // Pagination
    int getCurrentPage(Player player, InventoryType type);
    void setCurrentPage(Player player, InventoryType type, int page);

    void initializePageableTypes();

    void validatePlayer(Player player);

    void validateInventoryType(InventoryType type);

    void validatePageable(InventoryType type);

    boolean isInventoryPageable(InventoryType type);
    void setInventoryPageable(InventoryType type, boolean pageable);
    Set<InventoryType> getPageableInventoryTypes();
    
    // Inventory creation
    Inventory openCustomInventory(Player player, String title, int size, InventoryType type);
    Inventory openCustomInventory(Player player, String title, int size, InventoryType type, int page);
    InventoryBuilder createInventory(Player player);
    
    // Specialized menus
    void openConfirmMenu(Player player, String title);

    void openVotingInventory(Player player);

    void openConfirmMenu(Player player);
}