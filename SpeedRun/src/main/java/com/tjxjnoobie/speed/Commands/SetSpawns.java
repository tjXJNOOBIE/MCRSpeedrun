package com.tjxjnoobie.speed.Commands;

import com.tjxjnoobie.api.interfaces.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class SetSpawns implements CommandExecutor, IUtils {


    private final ISpeedRunContext speedRunContext;


    public SetSpawns(ISpeedRunContext speedRunContext) {
        this.speedRunContext = speedRunContext;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        IWorldManager worldManager = speedRunContext.getWorldManager();
        ILocationCache locationCache = speedRunContext.getLocationCache();
        IUtils utils = speedRunContext.getUtils();
        IGameType gameType = speedRunContext.getGameType();
        Player player = (Player) sender;
        double X = player.getLocation().getX();
        double Y = player.getLocation().getY();
        double Z = player.getLocation().getZ();
        float pitch = player.getLocation().getPitch();
        float yaw = player.getLocation().getYaw();
        String worldName = player.getLocation().getWorld().getName();
        String gameTypeText = gameType.getGameType().toString();
        int length = args.length;
        if(length > 0){
            player.sendMessage(getStaffPrefix()+"Usage: /setspawn");
        }else{
            try {
                worldManager.saveWorldSpawn(gameTypeText,worldName,X,Y,Z,pitch,yaw);
                worldManager.setIsSpawn(1,worldName);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            locationCache.removeLocationCache(worldName);
            locationCache.loadLocationCache();
            player.sendMessage(getStaffPrefix()+"You set spawn to X: " + X+" Y: "+Y+ " Z: "+Z);
        }
        return false;
    }
}
