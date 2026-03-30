package com.tjxjnoobie.api.platform.minecraft.managers;

import com.tjxjnoobie.api.platform.global.annotations.Inject;
import com.tjxjnoobie.api.platform.minecraft.inventory.interfaces.IInventoryManager;
import com.tjxjnoobie.api.platform.minecraft.inventory.builders.InventoryBuilder;
import com.tjxjnoobie.api.enums.InventoryType;
import com.tjxjnoobie.api.exceptions.InvalidInventoryTypeException;
import com.tjxjnoobie.api.exceptions.InvalidPageNumberException;
import com.tjxjnoobie.api.exceptions.InventoryCreationException;
import com.tjxjnoobie.api.exceptions.InventoryNotPageableException;
import com.tjxjnoobie.api.interfaces.*;
import com.tjxjnoobie.api.platform.minecraft.inventory.metadata.InventoryHistory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public  class InventoryManager implements IInventoryManager, IMinecraftDebuggable, IUtils {
    
   @Inject private IVoting voting;
    // Instance maps to track player inventory types
    private final Map<UUID, InventoryType> playerInventoryTypes = new HashMap<>();
    
    // Instance map to track player inventory history (stores last 10 inventories per player)
    private final Map<UUID, LinkedList<InventoryHistory>> playerInventoryHistory = new HashMap<>();
    
    // Instance map to track current page for paginated inventories
    private final Map<UUID, Map<InventoryType, Integer>> playerCurrentPages = new HashMap<>();
    
    // Set of inventory types that support pagination
    private final Set<InventoryType> pageableInventoryTypes = new HashSet<>();

    public InventoryManager() {
        // Initialize default pageable inventory types
        initializePageableTypes();
    }
    
    /**
     * Initialize default inventory types that support pagination
     */
    @Override
    public void initializePageableTypes() {
        pageableInventoryTypes.add(InventoryType.SHOP);
        pageableInventoryTypes.add(InventoryType.COSMETIC);
        pageableInventoryTypes.add(InventoryType.UPGRADE);
        pageableInventoryTypes.add(InventoryType.ACHIEVEMENTS);
        pageableInventoryTypes.add(InventoryType.LEADERBOARD);
        pageableInventoryTypes.add(InventoryType.CUSTOM);
        // CONFIRM, VOTING, SETTINGS, PROFILE typically don't need pagination
    }
    

    
    /**
     * Validates player and throws exception if null
     * @param player The player to validate
     * @throws IllegalArgumentException if player is null
     */
    @Override
    public void validatePlayer(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
    }
    
    /**
     * Validates inventory type and throws exception if null
     * @param type The inventory type to validate
     * @throws InvalidInventoryTypeException if type is null
     */
    @Override
    public void validateInventoryType(InventoryType type) {
        if (type == null) {
            throw new InvalidInventoryTypeException();
        }
    }
    
    /**
     * Validates that an inventory type supports pagination
     * @param type The inventory type to check
     * @throws InventoryNotPageableException if the type doesn't support pagination
     */
    @Override
    public void validatePageable(InventoryType type) {
        if (!isInventoryPageable(type)) {
            throw new InventoryNotPageableException(type);
        }
    }
    
    @Override
    public boolean isInventoryPageable(InventoryType type) {
        validateInventoryType(type);
        return pageableInventoryTypes.contains(type);
    }
    
    @Override
    public void setInventoryPageable(InventoryType type, boolean pageable) {
        validateInventoryType(type);
        
        if (pageable) {
            pageableInventoryTypes.add(type);
            sendDebugMessage(null, "[MANAGER]", "Set " + type.name() + " as pageable");
        } else {
            pageableInventoryTypes.remove(type);
            sendDebugMessage(null, "[MANAGER]", "Set " + type.name() + " as non-pageable");
        }
    }
    
    @Override
    public Set<InventoryType> getPageableInventoryTypes() {
        return new HashSet<>(pageableInventoryTypes);
    }
    
    @Override
    public void setPlayerInventoryType(Player player, InventoryType type, String title, int size, int page) {
        try {
            validatePlayer(player);
            validateInventoryType(type);
            
            // If page > 1, validate that the inventory type supports pagination
            if (page > 1) {
                validatePageable(type);
            }
            
            String safeTitle = title != null ? title : "Unknown";
            UUID playerId = player.getUniqueId();
            
            // Store previous inventory in history before setting new one
            InventoryType currentType = playerInventoryTypes.get(playerId);
            if (currentType != null && currentType != type) {
                addToHistory(player, currentType, safeTitle, size, page);
            }
            
            // Set new inventory type
            playerInventoryTypes.put(playerId, type);
            
            // Update current page for this inventory type
            playerCurrentPages.computeIfAbsent(playerId, k -> new HashMap<>()).put(type, page);
            
            sendDebugMessage(player, "[MANAGER]", "Set inventory type: " + type.name() + ", Title: " + safeTitle + ", Page: " + page);
            
        } catch (Exception e) {
            sendDebugMessage(player, "[MANAGER]", "Error in setPlayerInventoryType: " + e.getMessage());
            throw e;
        }
    }
    
    @Override
    public void setPlayerInventoryType(Player player, InventoryType type) {
        setPlayerInventoryType(player, type, "Unknown", 27, 1);
    }
    
    /**
     * Adds an inventory to the player's history
     * @param player The player
     * @param type The inventory type
     * @param title The inventory title
     * @param size The inventory size
     * @param page The page number
     */
    @Override
    public void addToHistory(Player player, InventoryType type, String title, int size, int page) {
        try {
            validatePlayer(player);
            validateInventoryType(type);
            
            UUID playerId = player.getUniqueId();
            LinkedList<InventoryHistory> history = playerInventoryHistory.computeIfAbsent(playerId, k -> new LinkedList<>());
            
            // Add to front of list (most recent first)
            InventoryHistory historyEntry = new InventoryHistory(type, title != null ? title : "Unknown", size, page);
            history.addFirst(historyEntry);
            
            // Keep only last 10 inventories
            while (history.size() > 10) {
                history.removeLast();
            }
            
            sendDebugMessage(player, "[MANAGER]", "Added to history: " + type.name() + " (History size: " + history.size() + ")");
            
        } catch (Exception e) {
            sendDebugMessage(player, "[MANAGER]", "Error in addToHistory: " + e.getMessage());
        }
    }
    
    @Override
    public InventoryType getPlayerInventoryType(Player player) {
        validatePlayer(player);
        
        UUID playerId = player.getUniqueId();
        InventoryType type = playerInventoryTypes.get(playerId);
        
        if (type == null) {
            sendDebugMessage(player, "[MANAGER]", "No inventory type found for player");
        }
        
        return type;
    }
    
    @Override
    public InventoryHistory getLastInventory(Player player) {
        validatePlayer(player);
        
        UUID playerId = player.getUniqueId();
        LinkedList<InventoryHistory> history = playerInventoryHistory.get(playerId);
        
        if (history == null || history.isEmpty()) {
            sendDebugMessage(player, "[MANAGER]", "No history found for player");
            return null;
        }
        
        return history.getFirst();
    }
    
    @Override
    public List<InventoryHistory> getInventoryHistory(Player player) {
        validatePlayer(player);
        
        UUID playerId = player.getUniqueId();
        LinkedList<InventoryHistory> history = playerInventoryHistory.get(playerId);
        
        return history != null ? new ArrayList<>(history) : new ArrayList<>();
    }
    
    @Override
    public int getCurrentPage(Player player, InventoryType type) {
        validatePlayer(player);
        validateInventoryType(type);
        
        UUID playerId = player.getUniqueId();
        Map<InventoryType, Integer> pages = playerCurrentPages.get(playerId);
        
        return (pages != null) ? pages.getOrDefault(type, 1) : 1;
    }
    
    @Override
    public void setCurrentPage(Player player, InventoryType type, int page) {
        validatePlayer(player);
        validateInventoryType(type);
        
        if (page <= 0) {
            throw new InvalidPageNumberException(page);
        }
        
        // If page > 1, validate that the inventory type supports pagination
        if (page > 1) {
            validatePageable(type);
        }
        
        UUID playerId = player.getUniqueId();
        playerCurrentPages.computeIfAbsent(playerId, k -> new HashMap<>()).put(type, page);
        
        sendDebugMessage(player, "[MANAGER]", "Set current page for " + type.name() + " to: " + page);
    }
    
    @Override
    public boolean openLastInventory(Player player) {
        try {
            validatePlayer(player);
            
            InventoryHistory lastInventory = getLastInventory(player);
            if (lastInventory == null) {
                sendDebugMessage(player, "[MANAGER]", "No last inventory found");
                return false;
            }
            
            UUID playerId = player.getUniqueId();
            
            // Remove the last inventory from history since we're going back to it
            LinkedList<InventoryHistory> history = playerInventoryHistory.get(playerId);
            if (history != null && !history.isEmpty()) {
                history.removeFirst();
                sendDebugMessage(player, "[MANAGER]", "Removed last inventory from history, remaining: " + history.size());
            }
            
            // Set the inventory type back to the previous one
            playerInventoryTypes.put(playerId, lastInventory.getType());
            
            sendDebugMessage(player, "[MANAGER]", "Opened last inventory: " + lastInventory.getType().name());
            
            return true;
            
        } catch (Exception e) {
            sendDebugMessage(player, "[MANAGER]", "Error in openLastInventory: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public void removePlayerInventoryType(Player player) {
        validatePlayer(player);
        
        UUID playerId = player.getUniqueId();
        InventoryType removedType = playerInventoryTypes.remove(playerId);
        
        sendDebugMessage(player, "[MANAGER]", "Removed inventory type tracking: " + (removedType != null ? removedType.name() : "null"));
    }
    
    @Override
    public void clearPlayerData(Player player) {
        validatePlayer(player);
        
        UUID playerId = player.getUniqueId();
        playerInventoryTypes.remove(playerId);
        playerInventoryHistory.remove(playerId);
        playerCurrentPages.remove(playerId);
        
        sendDebugMessage(player, "[MANAGER]", "Cleared all inventory data for player");
    }
    
    @Override
    public boolean hasInventoryType(Player player, InventoryType type) {
        validatePlayer(player);
        validateInventoryType(type);
        
        UUID playerId = player.getUniqueId();
        InventoryType currentType = playerInventoryTypes.get(playerId);
        boolean hasType = type.equals(currentType);
        
        sendDebugMessage(player, "[MANAGER]", "Checking inventory type " + type.name() + ": " + hasType + 
                        " (current: " + (currentType != null ? currentType.name() : "null") + ")");
        
        return hasType;
    }

    @Override
    public Inventory openCustomInventory(Player player, String title, int size, InventoryType type) {
        try {
            validatePlayer(player);
            validateInventoryType(type);
            
            String safeTitle = title != null ? title : "Custom Inventory";
            
            Inventory inv = Bukkit.createInventory(null, size, safeTitle);
            if (inv == null) {
                throw new InventoryCreationException(safeTitle, size);
            }
            
            setPlayerInventoryType(player, type, safeTitle, size, 1);
            player.openInventory(inv);
            
            sendDebugMessage(player, "[MANAGER]", "Opened custom inventory: " + type.name() + " (" + safeTitle + ")");
            
            return inv;
            
        } catch (Exception e) {
            sendDebugMessage(player, "[MANAGER]", "Error in openCustomInventory: " + e.getMessage());
            throw e;
        }
    }
    
    @Override
    public Inventory openCustomInventory(Player player, String title, int size, InventoryType type, int page) {
        try {
            validatePlayer(player);
            validateInventoryType(type);
            
            if (page <= 0) {
                throw new InvalidPageNumberException(page);
            }
            
            // If page > 1, validate that the inventory type supports pagination
            if (page > 1) {
                validatePageable(type);
            }
            
            String safeTitle = title != null ? title : "Custom Inventory";
            
            Inventory inv = Bukkit.createInventory(null, size, safeTitle);
            if (inv == null) {
                throw new InventoryCreationException(safeTitle, size);
            }
            
            setPlayerInventoryType(player, type, safeTitle, size, page);
            player.openInventory(inv);
            
            sendDebugMessage(player, "[MANAGER]", "Opened paginated inventory: " + type.name() + " (" + safeTitle + ") - Page " + page);
            
            return inv;
            
        } catch (Exception e) {
            sendDebugMessage(player, "[MANAGER]", "Error in openCustomInventory (paginated): " + e.getMessage());
            throw e;
        }
    }
    
    @Override
    public InventoryBuilder createInventory(Player player) {
        try {
            validatePlayer(player);
            
            InventoryBuilder builder = new InventoryBuilder(player, this);
            sendDebugMessage(player, "[MANAGER]", "Created new InventoryBuilder");
            
            return builder;
            
        } catch (Exception e) {
            sendDebugMessage(player, "[MANAGER]", "Error in createInventory: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void openConfirmMenu(Player player, String title) {
        try {
            validatePlayer(player);
            
            String safeTitle = title != null ? title : "§4§lAre you sure?";
            
            InventoryBuilder builder = createInventory(player);
            builder.title(safeTitle)
                .size(27)
                .type(InventoryType.CONFIRM)
                .item(11, Material.RED_WOOL, "§c§lDENY", "§7Click to deny")
                .item(13, Material.GREEN_WOOL, "§a§lCONFIRM", "§7Click to confirm")
                .item(15, Material.GREEN_WOOL, "§a§lCONFIRM", "§7Click to confirm")
                .fillEmpty(Material.BLACK_STAINED_GLASS_PANE)
                .open();
            
            sendDebugMessage(player, "[MANAGER]", "Successfully opened confirm menu: " + safeTitle);
            
        } catch (Exception e) {
            sendDebugMessage(player, "[MANAGER]", "Error in openConfirmMenu: " + e.getMessage());
            throw e;
        }
    }
    @Override
    public void openVotingInventory(Player player) {
        try {
            validatePlayer(player);
            
            int size = 27; // A 3-row inventory

            
            if (voting == null) {
                sendDebugMessage(player, "[MANAGER]", "Voting system not available");
                return;
            }
            
            Inventory inv = Bukkit.createInventory(null, size, "§eVote for a Gamemode");
            Map<String, Integer> gameModes = voting.getGameModes();
            List<Integer> middleSlots = Arrays.asList(10, 11, 12, 13, 14, 15, 16); // Middle row slots

            List<String> gamemodes = new ArrayList<>(gameModes.keySet());

            for (int i = 0; i < Math.min(gamemodes.size(), middleSlots.size()); i++) {
                String gamemode = gamemodes.get(i);
                int votes = gameModes.get(gamemode);
                ItemStack item = new ItemStack(Material.PAPER); // Represent the gamemode
                ItemMeta meta = item.getItemMeta();

                if (meta != null) {
                    meta.setDisplayName("§e" + gamemode);
                    meta.setLore(Arrays.asList("§7Votes: §a" + votes, "§eClick to vote!"));
                    item.setItemMeta(meta);
                }

                inv.setItem(middleSlots.get(i), item);
            }

            setPlayerInventoryType(player, InventoryType.VOTING, "§eVote for a Gamemode", size, 1);
            player.openInventory(inv);
            
            sendDebugMessage(player, "[MANAGER]", "Opened voting inventory");
            
        } catch (Exception e) {
            sendDebugMessage(player, "[MANAGER]", "Error opening voting inventory: " + e.getMessage());
            throw new RuntimeException("Failed to open voting inventory", e);
        }
    }
    
    @Override
    public void openConfirmMenu(Player player) {
        openConfirmMenu(player, "§4§lAre you sure?");
    }

    // TODO: Try to inject default methods in impled interfaces
    //  instead of Overriding in a weird place

}