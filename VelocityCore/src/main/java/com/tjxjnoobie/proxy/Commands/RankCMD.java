package com.tjxjnoobie.proxy.commands;

import com.tjxjnoobie.api.interfaces.*;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.sql.SQLException;
import java.util.UUID;

public class RankCMD implements SimpleCommand, IUtils, IRankCache,IPlayerProfile, IRank, IProxyUtils {

    @Override
    public void execute(Invocation invocation) {

        CommandSource source = invocation.source();
        Player player = (Player) source;

        String name = player.getUsername();
        String[] args = invocation.arguments();
        MiniMessage miniMessage = MiniMessage.miniMessage();
        String usage = "Usage: /rank <set | remove> <name> <rank_name>";
        String doesntExist = "Rank does not exist!";
        String noPermission = "No permission";
        UUID uuid = ((Player) source).getUniqueId();
        Component existMessage = withStaffPrefix(doesntExist);
        Component usageMessage = withStaffPrefix(usage);
        Component noPermMessage = withPrefix(noPermission);

        if (args.length < 3 && (isAdmin(uuid) || hasCachedPermission(uuid,"network.rank"))) {
            source.sendMessage(usageMessage);
            return;
        }

        String action = args[0];
        String userName = args[1];
        if (isAdmin(uuid) || hasCachedPermission(uuid, "network.rank")) {
            try {
                if (action.equalsIgnoreCase("set") && args.length == 3) {
                    String rankName = args[2];

                    if (!rankExists(rankName)) {
                        source.sendMessage(existMessage);
                        return;
                    }
                    if (playerExistsFromUsername(userName, "player_profile", "profile")) {
                        sendMessageToPlayer(name, getStaffPrefixString() + "Player does not exist in database");
                        return;
                    }
                    String success = "Set rank for " + userName + " to " + rankName;
                    Component successMessage = withStaffPrefix(success);
                    setRankFromUsername(userName, rankName);
                    source.sendMessage(successMessage);
                    sendMessageToPlayer(name, "d");
                } else {
                    source.sendMessage(usageMessage);
                }
            } catch (SQLException e) {
                sendMessageToPlayer(name, getStaffPrefixString() + "An error occurred while updating ");
                e.printStackTrace();
            }
        }else{ 
            sendMessageToPlayer(name, getPrefixString() + "&cNo permission");

        }
    }
}
