package com.tjxjnoobie.kingdomFactions.Events;



import com.tjxjnoobie.api.interfaces.BlockPlaceHandler;
import com.tjxjnoobie.api.interfaces.InterfaceManager;
import com.tjxjnoobie.api.interfaces.MainInterFace;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.Plugin;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BlockPlace implements Listener, BlockPlaceHandler {

    private Plugin plugin;
    private MainInterFace mainInterFace;
    private BlockPlaceHandler blockPlaceHandler;

    public BlockPlace(){

    }
    
    public BlockPlace(Plugin plugin, MainInterFace mainInterFace) {
        this.plugin = plugin;
        this.mainInterFace = mainInterFace;


    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e){
        Location location = e.getBlock().getLocation();
        String worldName = location.getWorld().getName();
        System.out.println("Block place event ran");
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(); DataOutputStream dataOut = new DataOutputStream(out)) {
            System.out.println("Sending plugin message with data " + out.toString());
            dataOut.writeUTF("BLOCK_PLACE");
            dataOut.writeUTF(worldName);
            dataOut.writeInt(location.getBlockX());
            dataOut.writeInt(location.getBlockY());
            dataOut.writeInt(location.getBlockZ());
            dataOut.writeUTF(e.getBlock().getType().name());
            mainInterFace = InterfaceManager.getMainInterFace();
            mainInterFace.sendPluginMessage(out.toByteArray());
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
    public void handleBlockPlace(DataInputStream dataIn) throws IOException {
        String worldName = dataIn.readUTF();
        int x = dataIn.readInt();
        int y = dataIn.readInt();
        int z = dataIn.readInt();
        Material material = Material.valueOf(dataIn.readUTF());
        World world = Bukkit.getWorld(worldName);
        if (world != null) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                world.getBlockAt(x, y, z).setType(material);
            });
        }else{
            System.out.println("World is null, can't handle block placing");
        }
    }

}

