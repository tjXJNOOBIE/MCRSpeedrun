package com.tjxjnoobie.proxy.Commands;

import com.tjxjnoobie.api.interfaces.*;
import com.tjxjnoobie.api.platform.global.annotations.Inject;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.sql.SQLException;
import java.util.UUID;

public class RankCMD implements SimpleCommand, IUtils {

    @Inject private IGlobalContext globalContext;


    

    @Override
    public void execute(Invocation invocation) {
        IProxyUtils proxyUtils = globalContext.getProxyUtils();
        IPlayerProfile playerProfile = globalContext.getPlayerProfile();
        IRank rank = globalContext.getRank();
        IRankCache rankCache = globalContext.getRankCache();
        CommandSource source = invocation.source();
        Player player = (Player) source;

        String name = player.getUsername();
        String[] args = invocation.arguments();
        MiniMessage miniMessage = MiniMessage.miniMessage();
        String usage = "Usage: /rank <set | remove> <name> <rank_name>";
        String doesntExist = "Rank does not exist!";
        String noPermission = "No permission";
        UUID uuid = ((Player) source).getUniqueId();
        Component existMessage = proxyUtils.withStaffPrefix(doesntExist);
        Component usageMessage = proxyUtils.withStaffPrefix(usage);
        Component noPermMessage = proxyUtils.withPrefix(noPermission);

        if (args.length < 3 && (rankCache.isAdmin(uuid) || rankCache.hasPermission(uuid,"network.rank"))) {
            source.sendMessage(usageMessage);
            return;
        }

        String action = args[0];
        String userName = args[1];
        if (rankCache.isAdmin(uuid) || rankCache.hasPermission(uuid, "network.rank")) {
            try {
                if (action.equalsIgnoreCase("set") && args.length == 3) {
                    String rankName = args[2];

                    if (!rankCache.rankExists(rankName)) {
                        source.sendMessage(existMessage);
                        return;
                    }
                    if (playerProfile.playerExistsFromUsername(userName, "player_profile", "profile")) {
                        proxyUtils.sendMessageToPlayer(name, proxyUtils.getStaffPrefixString() + "Player does not exist in database");
                        return;
                    }
                    String success = "Set rank for " + userName + " to " + rankName;
                    Component successMessage = proxyUtils.withStaffPrefix(success);
                    rank.setRankFromUsername(userName, rankName);
                    source.sendMessage(successMessage);
                    proxyUtils.sendMessageToPlayer(name, "d");
                } else {
                    source.sendMessage(usageMessage);
                }
            } catch (SQLException e) {
                proxyUtils.sendMessageToPlayer(name, proxyUtils.getStaffPrefixString() + "An error occurred while updating rank.");
                e.printStackTrace();
            }
        }else{
            proxyUtils.sendMessageToPlayer(name, proxyUtils.getPrefixString() + "&cNo permission");

        }
    }
}
