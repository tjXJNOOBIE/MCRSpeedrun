package com.tjxjnoobie.speed.Events.bukkit;

import com.tjxjnoobie.api.enums.GameStateEnum;
import com.tjxjnoobie.api.interfaces.IGameManager;
import com.tjxjnoobie.api.interfaces.ISpeedRunContext;
import com.tjxjnoobie.api.platform.global.annotations.Inject;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class SpeedRunLoginEvent implements Listener, IGameManager {


    @Inject private ISpeedRunContext speedRunContext;



    @EventHandler
    public void onSpeedLogin(PlayerLoginEvent e) {
        GameStateEnum currentState = getCurrentState();
        if (currentState == GameStateEnum.STARTUP) {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Server is starting up");
        } else if (currentState == GameStateEnum.ENDING) {
            Player winner = getSpeedRunWinner();
            String winnerName = winner.getName();
            String winnerTime = getFinalTimeString(winner.getUniqueId());
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, 
                "The game has ended! The winner is: " + winnerName + " with Final Time: " + winnerTime);
        }
    }
}
