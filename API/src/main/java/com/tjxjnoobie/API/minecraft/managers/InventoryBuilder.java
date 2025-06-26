package com.tjxjnoobie.API.minecraft.managers;

import com.tjxjnoobie.API.GlobalContext;
import com.tjxjnoobie.enums.InventoryType;
import com.tjxjnoobie.exceptions.*;
import com.tjxjnoobie.interfaces.Debuggable;
import com.tjxjnoobie.interfaces.IInventoryBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class InventoryBuilder implements IInventoryBuilder, Debuggable {
    
    private final Player player;
    private final InventoryManager inventoryManager;
    private String title;
    private int size;
    private InventoryType type;
    private int page;
    private Inventory inventory;
    
    public InventoryBuilder(Player player, InventoryManager inventoryManager) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        if (inventoryManager == null) {
            throw new IllegalArgumentException("InventoryManager cannot be null");
        }
        
        this.player = player;
        this.inventoryManager = inventoryManager;
        this.size = 27; // Default chest size
        this.type = InventoryType.CUSTOM; // Default type
        this.page = 1; // Default page
        
        sendDebugMessage(player, "[BUILDER]", "Created InventoryBuilder with defaults");
    }
    
    @Override
    public GlobalContext getGlobalContext() {
        return inventoryManager.getGlobalContext();
    }
    
    /**
     * Validates inventory size
     * @param size The size to validate
     * @throws InvalidInventorySizeException if size is invalid
     */
    private void validateSize(int size) {
        if (size <= 0 || size % 9 != 0 || size > 54) {
            throw new InvalidInventorySizeException(size);
        }
    }
    
    /**
     * Validates page number and pagination support
     * @param page The page to validate
     * @throws InvalidPageNumberException if page is invalid
     * @throws InventoryNotPageableException if inventory type doesn't support pagination
     */
    private void validatePage(int page) {
        if (page <= 0) {
            throw new InvalidPageNumberException(page);
        }
        
        // If page > 1, check if the current inventory type supports pagination
        if (page > 1 && !inventoryManager.isInventoryPageable(type)) {
            throw new InventoryNotPageableException(type);
        }
    }
    
    /**
     * Validates material
     * @param material The material to validate
     * @param operation The operation being performed
     * @throws InvalidMaterialException if material is null
     */
    private void validateMaterial(Material material, String operation) {
        if (material == null) {
            throw new InvalidMaterialException(operation);
        }
    }
    
    /**
     * Validates slot number
     * @param slot The slot to validate
     * @throws InvalidSlotException if slot is invalid
     */
    private void validateSlot(int slot) {
        ensureInventoryCreated();
        if (slot < 0 || slot >= inventory.getSize()) {
            throw new InvalidSlotException(slot, inventory.getSize());
        }
    }
    
    @Override
    public IInventoryBuilder title(String title) {
        String safeTitle = title != null ? title : "Custom Inventory";
        this.title = safeTitle;
        sendDebugMessage(player, "[BUILDER]", "Set title: " + safeTitle);
        return this;
    }
    
    @Override
    public IInventoryBuilder size(int size) {
        try {
            validateSize(size);
            this.size = size;
            sendDebugMessage(player, "[BUILDER]", "Set size: " + size);
        } catch (InvalidInventorySizeException e) {
            sendDebugMessage(player, "[BUILDER]", "Invalid size (" + size + "), using default 27");
            this.size = 27;
        }
        return this;
    }
    
    @Override
    public IInventoryBuilder type(InventoryType type) {
        if (type == null) {
            sendDebugMessage(player, "[BUILDER]", "InventoryType is null, using CUSTOM");
            this.type = InventoryType.CUSTOM;
        } else {
            this.type = type;
            sendDebugMessage(player, "[BUILDER]", "Set type: " + type.name());
        }
        return this;
    }
    
    @Override
    public IInventoryBuilder page(int page) {
        try {
            validatePage(page);
            this.page = page;
            sendDebugMessage(player, "[BUILDER]", "Set page: " + page);
        } catch (InvalidPageNumberException e) {
            sendDebugMessage(player, "[BUILDER]", "Invalid page (" + page + "), using 1");
            this.page = 1;
        } catch (InventoryNotPageableException e) {
            sendDebugMessage(player, "[BUILDER]", "Inventory type " + type.name() + " doesn't support pagination, using page 1");
            this.page = 1;
        }
        return this;
    }
    
    @Override
    public IInventoryBuilder item(int slot, Material material) {
        try {
            validateMaterial(material, "item placement");
            validateSlot(slot);
            
            inventory.setItem(slot, new ItemStack(material));
            sendDebugMessage(player, "[BUILDER]", "Added item " + material.name() + " to slot " + slot);
            
        } catch (Exception e) {
            sendDebugMessage(player, "[BUILDER]", "Error adding item to slot " + slot + ": " + e.getMessage());
        }
        return this;
    }
    
    @Override
    public IInventoryBuilder item(int slot, Material material, String displayName) {
        try {
            validateMaterial(material, "item with display name");
            validateSlot(slot);
            
            String safeName = displayName != null ? displayName : material.name();
            
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(safeName);
                item.setItemMeta(meta);
            }
            
            inventory.setItem(slot, item);
            sendDebugMessage(player, "[BUILDER]", "Added item " + material.name() + " with name '" + safeName + "' to slot " + slot);
            
        } catch (Exception e) {
            sendDebugMessage(player, "[BUILDER]", "Error adding item with display name to slot " + slot + ": " + e.getMessage());
        }
        return this;
    }
    
    @Override
    public IInventoryBuilder item(int slot, Material material, String displayName, String... lore) {
        try {
            validateMaterial(material, "item with lore");
            validateSlot(slot);
            
            String safeName = displayName != null ? displayName : material.name();
            String[] safeLore = lore != null ? lore : new String[0];
            
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(safeName);
                meta.setLore(Arrays.asList(safeLore));
                item.setItemMeta(meta);
            }
            
            inventory.setItem(slot, item);
            sendDebugMessage(player, "[BUILDER]", "Added item " + material.name() + " with name '" + safeName + 
                            "' and " + safeLore.length + " lore lines to slot " + slot);
            
        } catch (Exception e) {
            sendDebugMessage(player, "[BUILDER]", "Error adding item with lore to slot " + slot + ": " + e.getMessage());
        }
        return this;
    }
    
    @Override
    public IInventoryBuilder item(int slot, Material material, String displayName, List<String> lore) {
        try {
            validateMaterial(material, "item with lore list");
            validateSlot(slot);
            
            String safeName = displayName != null ? displayName : material.name();
            List<String> safeLore = lore != null ? lore : Arrays.asList();
            
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(safeName);
                meta.setLore(safeLore);
                item.setItemMeta(meta);
            }
            
            inventory.setItem(slot, item);
            sendDebugMessage(player, "[BUILDER]", "Added item " + material.name() + " with name '" + safeName + 
                            "' and " + safeLore.size() + " lore lines to slot " + slot);
            
        } catch (Exception e) {
            sendDebugMessage(player, "[BUILDER]", "Error adding item with lore list to slot " + slot + ": " + e.getMessage());
        }
        return this;
    }
    
    @Override
    public IInventoryBuilder item(int slot, ItemStack itemStack) {
        try {
            if (itemStack == null) {
                throw new IllegalArgumentException("ItemStack cannot be null");
            }
            validateSlot(slot);
            
            inventory.setItem(slot, itemStack);
            sendDebugMessage(player, "[BUILDER]", "Added custom ItemStack " + itemStack.getType().name() + " to slot " + slot);
            
        } catch (Exception e) {
            sendDebugMessage(player, "[BUILDER]", "Error adding ItemStack to slot " + slot + ": " + e.getMessage());
        }
        return this;
    }
    
    @Override
    public IInventoryBuilder fillEmpty(Material material) {
        try {
            validateMaterial(material, "fill empty slots");
            ensureInventoryCreated();
            
            ItemStack fillItem = createFillItem(material);
            int filledSlots = 0;
            
            for (int i = 0; i < inventory.getSize(); i++) {
                if (inventory.getItem(i) == null) {
                    inventory.setItem(i, fillItem);
                    filledSlots++;
                }
            }
            
            sendDebugMessage(player, "[BUILDER]", "Filled " + filledSlots + " empty slots with " + material.name());
            
        } catch (Exception e) {
            sendDebugMessage(player, "[BUILDER]", "Error filling empty slots: " + e.getMessage());
        }
        return this;
    }
    
    @Override
    public IInventoryBuilder fill(Material material, int... slots) {
        try {
            validateMaterial(material, "fill specific slots");
            if (slots == null) {
                throw new IllegalArgumentException("Slots array cannot be null");
            }
            
            ensureInventoryCreated();
            ItemStack fillItem = createFillItem(material);
            int validSlots = 0;
            
            for (int slot : slots) {
                if (slot >= 0 && slot < inventory.getSize()) {
                    inventory.setItem(slot, fillItem);
                    validSlots++;
                } else {
                    sendDebugMessage(player, "[BUILDER]", "Invalid slot " + slot + " in fill(), inventory size: " + inventory.getSize());
                }
            }
            
            sendDebugMessage(player, "[BUILDER]", "Filled " + validSlots + "/" + slots.length + " slots with " + material.name());
            
        } catch (Exception e) {
            sendDebugMessage(player, "[BUILDER]", "Error filling specific slots: " + e.getMessage());
        }
        return this;
    }
    
    @Override
    public IInventoryBuilder fillRange(Material material, int startSlot, int endSlot) {
        try {
            validateMaterial(material, "fill range");
            ensureInventoryCreated();
            
            if (startSlot < 0 || endSlot >= inventory.getSize() || startSlot > endSlot) {
                throw new InvalidSlotException("Invalid range (" + startSlot + "-" + endSlot + ") for inventory size " + inventory.getSize());
            }
            
            ItemStack fillItem = createFillItem(material);
            
            for (int i = startSlot; i <= endSlot; i++) {
                inventory.setItem(i, fillItem);
            }
            
            sendDebugMessage(player, "[BUILDER]", "Filled range " + startSlot + "-" + endSlot + " with " + material.name());
            
        } catch (Exception e) {
            sendDebugMessage(player, "[BUILDER]", "Error filling range: " + e.getMessage());
        }
        return this;
    }
    
    /**
     * Creates a fill item with proper meta
     * @param material The material for the fill item
     * @return The created ItemStack
     */
    private ItemStack createFillItem(Material material) {
        ItemStack fillItem = new ItemStack(material);
        ItemMeta meta = fillItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§0");
            fillItem.setItemMeta(meta);
        }
        return fillItem;
    }
    
    @Override
    public IInventoryBuilder addPaginationControls(int currentPage, int totalPages) {
        try {
            if (currentPage <= 0 || totalPages <= 0 || currentPage > totalPages) {
                throw new InvalidPaginationException(currentPage, totalPages);
            }
            
            // Check if the current inventory type supports pagination
            if (!inventoryManager.isInventoryPageable(type)) {
                throw new InventoryNotPageableException(type);
            }
            
            ensureInventoryCreated();
            int inventorySize = inventory.getSize();
            
            // Previous page button (bottom left)
            if (currentPage > 1) {
                ItemStack prevButton = new ItemStack(Material.ARROW);
                ItemMeta prevMeta = prevButton.getItemMeta();
                if (prevMeta != null) {
                    prevMeta.setDisplayName("§e◀ Previous Page");
                    prevMeta.setLore(Arrays.asList("§7Page " + (currentPage - 1) + "/" + totalPages, "§eClick to go back"));
                    prevButton.setItemMeta(prevMeta);
                }
                inventory.setItem(inventorySize - 9, prevButton);
            }
            
            // Page info (bottom center)
            ItemStack pageInfo = new ItemStack(Material.BOOK);
            ItemMeta pageInfoMeta = pageInfo.getItemMeta();
            if (pageInfoMeta != null) {
                pageInfoMeta.setDisplayName("§6Page " + currentPage + "/" + totalPages);
                pageInfoMeta.setLore(Arrays.asList("§7You are viewing page " + currentPage));
                pageInfo.setItemMeta(pageInfoMeta);
            }
            inventory.setItem(inventorySize - 5, pageInfo);
            
            // Next page button (bottom right)
            if (currentPage < totalPages) {
                ItemStack nextButton = new ItemStack(Material.ARROW);
                ItemMeta nextMeta = nextButton.getItemMeta();
                if (nextMeta != null) {
                    nextMeta.setDisplayName("§eNext Page ▶");
                    nextMeta.setLore(Arrays.asList("§7Page " + (currentPage + 1) + "/" + totalPages, "§eClick to continue"));
                    nextButton.setItemMeta(nextMeta);
                }
                inventory.setItem(inventorySize - 1, nextButton);
            }
            
            sendDebugMessage(player, "[BUILDER]", "Added pagination controls (page " + currentPage + "/" + totalPages + ")");
            
        } catch (Exception e) {
            sendDebugMessage(player, "[BUILDER]", "Error adding pagination controls: " + e.getMessage());
        }
        return this;
    }
    
    @Override
    public IInventoryBuilder addBackButton() {
        try {
            ensureInventoryCreated();
            
            InventoryHistory lastInventory = inventoryManager.getLastInventory(player);
            if (lastInventory != null) {
                ItemStack backButton = new ItemStack(Material.BARRIER);
                ItemMeta backMeta = backButton.getItemMeta();
                if (backMeta != null) {
                    backMeta.setDisplayName("§c◀ Back");
                    backMeta.setLore(Arrays.asList(
                        "§7Return to: §e" + lastInventory.getTitle(),
                        "§7Type: §e" + lastInventory.getType().name(),
                        "§eClick to go back"
                    ));
                    backButton.setItemMeta(backMeta);
                }
                
                int slot = inventory.getSize() - 8; // Bottom row, second slot
                inventory.setItem(slot, backButton);
                sendDebugMessage(player, "[BUILDER]", "Added back button to slot " + slot);
            } else {
                sendDebugMessage(player, "[BUILDER]", "No history found, skipping back button");
            }
            
        } catch (Exception e) {
            sendDebugMessage(player, "[BUILDER]", "Error adding back button: " + e.getMessage());
        }
        return this;
    }
    
    /**
     * Creates the inventory if it hasn't been created yet
     */
    private void ensureInventoryCreated() {
        if (inventory == null) {
            try {
                String finalTitle = title != null ? title : "Custom Inventory";
                if (page > 1) {
                    finalTitle += " §7(Page " + page + ")";
                }
                
                inventory = Bukkit.createInventory(null, size, finalTitle);
                if (inventory == null) {
                    throw new InventoryCreationException(finalTitle, size);
                }
                
                sendDebugMessage(player, "[BUILDER]", "Created inventory: " + finalTitle + " (size: " + size + ")");
                
            } catch (Exception e) {
                sendDebugMessage(player, "[BUILDER]", "Error creating inventory: " + e.getMessage());
                throw new InventoryCreationException("Failed to create inventory", e);
            }
        }
    }
    
    @Override
    public Inventory open() {
        try {
            ensureInventoryCreated();
            
            inventoryManager.setPlayerInventoryType(player, type, title, size, page);
            player.openInventory(inventory);
            
            sendDebugMessage(player, "[BUILDER]", "Successfully opened inventory: " + type.name() + " (page " + page + ")");
            
            return inventory;
            
        } catch (Exception e) {
            sendDebugMessage(player, "[BUILDER]", "Error opening inventory: " + e.getMessage());
            throw e;
        }
    }
    
    @Override
    public Inventory build() {
        try {
            ensureInventoryCreated();
            sendDebugMessage(player, "[BUILDER]", "Built inventory: " + type.name() + " (page " + page + ")");
            return inventory;
            
        } catch (Exception e) {
            sendDebugMessage(player, "[BUILDER]", "Error building inventory: " + e.getMessage());
            throw e;
        }
    }
}