package com.tjxjnoobie.website.account;

import com.tjxjnoobie.store.domain.model.UsernameResolution;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AccountSessionService {

    public static final String PLAYER_UUID_SESSION_KEY = "store.player.uuid";
    public static final String PLAYER_USERNAME_SESSION_KEY = "store.player.username";

    public Optional<PlayerAccountSession> current(HttpSession session) {
        Object uuidValue = session.getAttribute(PLAYER_UUID_SESSION_KEY);
        Object usernameValue = session.getAttribute(PLAYER_USERNAME_SESSION_KEY);
        if (!(uuidValue instanceof String uuidText) || !(usernameValue instanceof String username)) {
            return Optional.empty();
        }
        return Optional.of(new PlayerAccountSession(UUID.fromString(uuidText), username));
    }

    public PlayerAccountSession require(HttpSession session) {
        return current(session).orElseThrow(() -> new IllegalStateException("No verified player session"));
    }

    public void establish(HttpSession session, UsernameResolution resolution) {
        session.setAttribute(PLAYER_UUID_SESSION_KEY, resolution.uuid().toString());
        session.setAttribute(PLAYER_USERNAME_SESSION_KEY, resolution.normalizedUsername());
    }

    public void clear(HttpSession session) {
        session.removeAttribute(PLAYER_UUID_SESSION_KEY);
        session.removeAttribute(PLAYER_USERNAME_SESSION_KEY);
    }
}
