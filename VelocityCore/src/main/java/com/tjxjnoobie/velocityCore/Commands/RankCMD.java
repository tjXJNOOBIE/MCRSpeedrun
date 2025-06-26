package com.tjxjnoobie.velocityCore.Commands;

import com.tjxjnoobie.API.cache.RankCache;
import com.tjxjnoobie.API.GlobalContext;
import com.tjxjnoobie.API.managers.PlayerProfile;
import com.tjxjnoobie.API.velocity.Rank;
import com.tjxjnoobie.API.utils.ProxyUtils;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.sql.SQLException;
import java.util.UUID;

public class RankCMD implements SimpleCommand {

    private final GlobalContext globalContext;


    public RankCMD(GlobalContext globalContext) {
        this.globalContext = globalContext;

    }

    @Override
    public void execute(Invocation invocation) {
        ProxyUtils proxyUtils = globalContext.getProxyUtils();
        PlayerProfile playerProfile = globalContext.getPlayerProfile();
        Rank rank = globalContext.getRank();
        RankCache rankCache = globalContext.getRankCache();
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
                        proxyUtils.sendMessageToPlayer(name, proxyUtils.staffPrefix + "Player does not exist in database");
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
                proxyUtils.sendMessageToPlayer(name, proxyUtils.preifxString + "An error occurred while updating rank.");
                e.printStackTrace();
            }
        }else{
            proxyUtils.sendMessageToPlayer(name, proxyUtils.preifxString + "&cNo permission");

        }
    }
}
