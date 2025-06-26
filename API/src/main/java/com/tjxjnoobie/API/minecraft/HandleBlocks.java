package com.tjxjnoobie.API.minecraft;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public class HandleBlocks {

    private Plugin plugin;


    public void handleBlockPlace(String[] parts) {
        if (parts.length < 6) return;

        String worldName = parts[1];
        int x = Integer.parseInt(parts[2]);
        int y = Integer.parseInt(parts[3]);
        int z = Integer.parseInt(parts[4]);
        Material material = Material.valueOf(parts[5]);

        World world = Bukkit.getWorld(worldName);
        if (world != null) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                world.getBlockAt(x, y, z).setType(material);
            });
        }
    }
}
