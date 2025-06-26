package com.tjxjnoobie.speed.Events;

import com.comphenix.protocol.PacketType;
import com.tjxjnoobie.API.utils.Utils;
import com.tjxjnoobie.speed.managers.SpeedRunContext;
import com.tjxjnoobie.speed.managers.Voting;
import okio.Utf8;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class InventoryClick implements Listener {

    private final SpeedRunContext speedRunContext;
    public InventoryClick(SpeedRunContext speedRunContext) {
        this.speedRunContext = speedRunContext;
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) throws SQLException {
        Voting voting = speedRunContext.getVoting();
        Utils utils = speedRunContext.getUtils();
        Player player = (Player) e.getWhoClicked();
        UUID uuid = player.getUniqueId();

        boolean hasVoted = voting.getHasVotedHash().contains(uuid);
        if (e.getView().getTitle().equals("§aVote for a Gamemode") && e.getClickedInventory() != null) {
            if(hasVoted){
                player.sendMessage(utils.prefix+"§cYou have already voted!");
                e.setCancelled(true);
                player.closeInventory();
                return;
            }


            e.setCancelled(true);
            ItemStack clickedItem = e.getCurrentItem();
            if (clickedItem != null && clickedItem.getType() == Material.PAPER && clickedItem.getItemMeta() != null) {
                String gamemode = clickedItem.getItemMeta().getDisplayName().replace("§e", "");
                voting.vote(player,gamemode);
                player.sendMessage(utils.prefix+"§aYou voted for " + gamemode + "!");
                voting.getHasVotedHash().add(uuid);

                player.closeInventory();
            }
        }
    }
}
