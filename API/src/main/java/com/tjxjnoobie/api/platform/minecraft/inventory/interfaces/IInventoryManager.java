package com.tjxjnoobie.api.platform.minecraft.inventory.interfaces;


import com.tjxjnoobie.api.platform.minecraft.inventory.metadata.InventoryHistory;
import com.tjxjnoobie.api.platform.minecraft.inventory.builders.InventoryBuilder;
import com.tjxjnoobie.api.enums.InventoryType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Set;

/**
 * Interface for InventoryManager to provide inventory management capabilities
 */
public interface IInventoryManager extends com.tjxjnoobie.api.dependency.IDependencyInjectableInterface {

    // Core inventory operations

    default void setPlayerInventoryType(Player player, InventoryType type, String title, int size, int page) {
    }

    default void setPlayerInventoryType(Player player, InventoryType type) {
    }

    default void addToHistory(Player player, InventoryType type, String title, int size, int page) {
    }

    default InventoryType getPlayerInventoryType(Player player) {
        return null;
    }

    default void removePlayerInventoryType(Player player) {
    }

    default void clearPlayerData(Player player) {
    }

    default boolean hasInventoryType(Player player, InventoryType type) {
        return false;
    }

    // History management
    default InventoryHistory getLastInventory(Player player) {
        return null;
    }

    default List<InventoryHistory> getInventoryHistory(Player player) {
        return java.util.Collections.emptyList();
    }

    default boolean openLastInventory(Player player) {
        return false;
    }

    // Pagination
    default int getCurrentPage(Player player, InventoryType type) {
        return 0;
    }

    default void setCurrentPage(Player player, InventoryType type, int page) {
    }

    default void initializePageableTypes() {
    }

    default void validatePlayer(Player player) {
    }

    default void validateInventoryType(InventoryType type) {
    }

    default void validatePageable(InventoryType type) {
    }

    default boolean isInventoryPageable(InventoryType type) {
        return false;
    }

    default void setInventoryPageable(InventoryType type, boolean pageable) {
    }

    default Set<InventoryType> getPageableInventoryTypes() {
        return java.util.Collections.emptySet();
    }

    // Inventory creation
    default Inventory openCustomInventory(Player player, String title, int size, InventoryType type) {
        return null;
    }

    default Inventory openCustomInventory(Player player, String title, int size, InventoryType type, int page) {
        return null;
    }

    default InventoryBuilder createInventory(Player player) {
        return null;
    }

    // Specialized menus
    default void openConfirmMenu(Player player, String title) {
    }

    default void openVotingInventory(Player player) {
    }

    default void openConfirmMenu(Player player) {
    }
}
