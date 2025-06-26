package com.tjxjnoobie.speed.Events;

import com.tjxjnoobie.API.GlobalContext;
import com.tjxjnoobie.interfaces.IInventoryManager;
import com.tjxjnoobie.speed.managers.SpeedRunContext;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SpeedRunInventoryInteract implements Listener {

    private final SpeedRunContext speedRunContext;
    private final GlobalContext globalContext;

    public SpeedRunInventoryInteract(SpeedRunContext speedRunContext, GlobalContext globalContext) {
        this.speedRunContext = speedRunContext;
        this.globalContext = globalContext;
    }

    @EventHandler
    public void onInteract(InventoryInteractEvent e) {
        IInventoryManager inventoryManager = globalContext.getInventoryManager();
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
