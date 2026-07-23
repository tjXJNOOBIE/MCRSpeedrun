package com.tjxjnoobie.speed.Events.bukkit;

import com.tjxjnoobie.api.platform.minecraft.inventory.interfaces.IInventoryManager;
import org.tavall.dependency.annotations.Inject;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SpeedRunInventoryInteract implements Listener {

    @Inject private IInventoryManager inventoryManager;


    @EventHandler
    public void onInteract(InventoryInteractEvent e) {
        Player player = (Player) e.getWhoClicked();
        ItemStack handItem = player.getInventory().getItemInMainHand();
        ItemMeta handMeta = handItem.getItemMeta();

        if (e.getView().getTitle().equals("§eVote for a Gamemode")) {
            e.setCancelled(true);
        }
        if (handItem.getType() == Material.PAPER && handMeta.getDisplayName().equals("§aVote for a gamemode!")) {

            inventoryManager.openVotingInventory(player);
            e.setCancelled(true);

        }
    }
}