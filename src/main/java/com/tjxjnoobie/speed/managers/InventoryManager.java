package com.tjxjnoobie.speed.managers;

import com.tjxjnoobie.interfaces.IGameState;
import com.tjxjnoobie.interfaces.IVoting;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class InventoryManager {

    private final SpeedRunContext speedRunContext;


    private HashMap<String, Integer> gameMode;


    public InventoryManager(SpeedRunContext speedRunContext) {
        this.speedRunContext = speedRunContext;

    }


}
