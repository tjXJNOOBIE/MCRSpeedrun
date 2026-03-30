package com.tjxjnoobie.speed.Events.bukkit;

import com.tjxjnoobie.api.platform.minecraft.utils.interfaces.IMCUtils;
import com.tjxjnoobie.api.interfaces.IVoting;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.UUID;

public class SpeedRunInventoryClick implements Listener, IMCUtils, IVoting {



    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) throws SQLException {
        
        Player player = (Player) e.getWhoClicked();
        UUID uuid = player.getUniqueId();

        boolean hasVoted = getHasVotedHash().contains(uuid);
        if (e.getView().getTitle().equals("§aVote for a Gamemode") && e.getClickedInventory() != null) {
            if(hasVoted){
                player.sendMessage(getMinecraftPrefix()+"§cYou have already voted!");
                e.setCancelled(true);
                player.closeInventory();
                return;
            }


            e.setCancelled(true);
            ItemStack clickedItem = e.getCurrentItem();
            if (clickedItem != null && clickedItem.getType() == Material.PAPER && clickedItem.getItemMeta() != null) {
                String gamemode = clickedItem.getItemMeta().getDisplayName().replace("§e", "");
                vote(player,gamemode);
                player.sendMessage(getMinecraftPrefix()+"§aYou voted for " + gamemode + "!");
                getHasVotedHash().add(uuid);

                player.closeInventory();
            }
        }
    }
}