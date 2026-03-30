package com.tjxjnoobie.api.platform.minecraft.inventory.interfaces;

import com.tjxjnoobie.api.enums.InventoryType;
import com.tjxjnoobie.api.interfaces.IMinecraftDebuggable;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Interface for InventoryBuilder to provide fluent inventory building capabilities
 */
public interface IInventoryBuilder extends IMinecraftDebuggable {
    
    // Basic configuration
    IInventoryBuilder title(String title);
    IInventoryBuilder size(int size);
    IInventoryBuilder type(InventoryType type);
    IInventoryBuilder page(int page);
    
    // Item placement
    IInventoryBuilder item(int slot, Material material);
    IInventoryBuilder item(int slot, Material material, String displayName);
    IInventoryBuilder item(int slot, Material material, String displayName, String... lore);
    IInventoryBuilder item(int slot, Material material, String displayName, List<String> lore);
    IInventoryBuilder item(int slot, ItemStack itemStack);
    
    // Fill operations
    IInventoryBuilder fillEmpty(Material material);
    IInventoryBuilder fill(Material material, int... slots);
    IInventoryBuilder fillRange(Material material, int startSlot, int endSlot);
    
    // Special features
    IInventoryBuilder addPaginationControls(int currentPage, int totalPages);
    IInventoryBuilder addBackButton();
    
    // Build operations
    Inventory open();
    Inventory build();
}