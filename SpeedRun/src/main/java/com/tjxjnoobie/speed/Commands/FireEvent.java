package com.tjxjnoobie.speed.Commands;

import com.tjxjnoobie.api.interfaces.IGlobalContext;
import com.tjxjnoobie.api.interfaces.ISpeedRunContext;
import com.tjxjnoobie.api.interfaces.IUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command to fire events using their interface implementations
 * Usage: /fireevent <EventClassName> [additional args]
 */
public class FireEvent<T extends IGlobalContext> implements CommandExecutor, IUtils {
    T context;

    private final ISpeedRunContext speedRunContext;



    public FireEvent(ISpeedRunContext speedRunContext) {
        this.speedRunContext = speedRunContext;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.isOp()) {
            player.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage("§cUsage: /fireevent <EventClassName>");
            player.sendMessage("§eAvailable events: JoinEvent, QuitEvent, MobKill, DeathEvent, etc.");
            return true;
        }

        String eventClassName = args[0];

        try {
            //TODO Implement the logic to fire the event using its interface implementation fireEventByInterface(eventClassName, args);
            player.sendMessage("§aSuccessfully fired event: " + eventClassName);
        } catch (Exception e) {
            player.sendMessage("§cFailed to fire event: " + eventClassName);
            player.sendMessage("§cError: " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }
}

