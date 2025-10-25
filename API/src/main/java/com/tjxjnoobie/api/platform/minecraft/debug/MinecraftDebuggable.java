package com.tjxjnoobie.api.platform.minecraft.debug;

import com.tjxjnoobie.api.interfaces.IMinecraftDebuggable;
import com.tjxjnoobie.api.interfaces.ISpeedRunContext;
import com.tjxjnoobie.api.platform.global.annotations.Inject;
import org.bukkit.entity.Player;

public class MinecraftDebuggable implements IMinecraftDebuggable {

    @Inject
    private ISpeedRunContext speedRunContext;

    @Override
    public ISpeedRunContext getSpeedRunContext() {
        return speedRunContext;
    }

    @Override
    public void sendDebugMessage(Player player, String message) {
        if (player != null) {
            boolean isDebugger = getSpeedRunContext().getDebugger().isDebugger(player.getUniqueId());
            if (isDebugger) {
                String staffPrefix = getSpeedRunContext().getUtils().getStaffPrefix();
                player.sendMessage(staffPrefix + "§c" + message);
            }
        }
    }

    @Override
    public void sendDebugMessage(Player player, String prefix, String message) {
        if (player != null) {
            boolean isDebugger = getSpeedRunContext().getDebugger().isDebugger(player.getUniqueId());
            if (isDebugger) {
                String staffPrefix = getSpeedRunContext().getUtils().getStaffPrefix();
                player.sendMessage(staffPrefix + "§c" + prefix + " " + message);
            }
        }
    }
}
