package com.tjxjnoobie.speed.Commands;

import com.tjxjnoobie.api.interfaces.IGlobalContext;
import com.tjxjnoobie.api.interfaces.IRankCache;
import com.tjxjnoobie.api.interfaces.IUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class SpawnEnderDragon implements CommandExecutor, IUtils {


    private final IGlobalContext globalContext;

    public SpawnEnderDragon(IGlobalContext globalContext) {
        this.globalContext = globalContext;
    }

    @Override
    public boolean onCommand (CommandSender sender,  Command command, String label, String [] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();
            World world = player.getWorld();
            Location location = player.getLocation();
            IRankCache rankCache = globalContext.getRankCache();
            if(rankCache.getPowerLevel(uuid) <= 10000 ||
                rankCache.hasPermission(uuid,"speedrun.spawndragon")) {
                spawnStationaryEnderDragon(world, location);
                player.sendMessage(staffPrefix + " Ender dragon spawned");
                player.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD,1));
                return true;
            } else {
                player.sendMessage(prefix+ "§cNo Permission.");
            }
        }else{
            sender.sendMessage("Only a player may issue this command");

        }
        return false;
    }

    public void spawnStationaryEnderDragon(World world, Location location) {
        EnderDragon enderDragon = (EnderDragon) world.spawnEntity(location, EntityType.ENDER_DRAGON);
        enderDragon.setHealth(1.0);
        enderDragon.setPhase(EnderDragon.Phase.HOVER);
    }
    }

