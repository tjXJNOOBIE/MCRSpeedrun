package com.tjxjnoobie.api.platform.minecraft.debug;

import com.tjxjnoobie.api.interfaces.IDebugger;
import com.tjxjnoobie.api.interfaces.IMCUtils;
import com.tjxjnoobie.api.interfaces.ISpeedRunContext;
import com.tjxjnoobie.api.platform.global.annotations.Inject;
import org.bukkit.entity.Player;

public class MinecraftDebuggable implements IDebugger, IMCUtils {

    @Inject
    private ISpeedRunContext speedRunContext;

    @Override
    public ISpeedRunContext getSpeedRunContext() {
        return speedRunContext;
    }

    @Override
    public void sendDebugMessage(Player player, String message) {
        if (player != null) {
            boolean isDebugger = isDebugger(player.getUniqueId());
            if (isDebugger) {
                player.sendMessage(getMinecraftStaffInGamePrefix() + "§c" + message);
            }
        }
    }

    @Override
    public void sendDebugMessage(Player player, String prefix, String message) {
        if (player != null) {
            boolean isDebugger = isDebugger(player.getUniqueId());
            if (isDebugger) {
                player.sendMessage(getMinecraftStaffInGamePrefix() + "§c" + prefix + " " + message);
            }
        }
    }
}
