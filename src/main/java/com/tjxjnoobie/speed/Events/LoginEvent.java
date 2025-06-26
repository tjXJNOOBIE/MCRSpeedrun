package com.tjxjnoobie.speed.Events;

import com.tjxjnoobie.API.managers.GameState;
import com.tjxjnoobie.enums.GameStateEnum;
import com.tjxjnoobie.speed.managers.GameManager;
import com.tjxjnoobie.speed.managers.SpeedRunContext;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.UUID;

public class LoginEvent implements Listener {


    private final SpeedRunContext speedRunContext;

    public LoginEvent(SpeedRunContext speedRunContext) {
        this.speedRunContext = speedRunContext;
    }

    @EventHandler
    public void onSpeedLogin(PlayerLoginEvent e) {
        GameStateEnum currentState = speedRunContext.getGameState().getCurrentState();
        if (currentState == GameStateEnum.STARTUP) {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Server is starting up");
        } else if (currentState == GameStateEnum.ENDING) {
            GameManager gameManager = speedRunContext.getGameManager();
            Player winner = gameManager.getWinner();
            String winnerName = winner.getName();
            String winnerTime = gameManager.getFinalTimeString(winner.getUniqueId());
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, 
                "The game has ended! The winner is: " + winnerName + " with Final Time: " + winnerTime);
        }
    }
}
