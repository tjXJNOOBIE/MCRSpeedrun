package com.tjxjnoobie.proxy;

import com.tjxjnoobie.api.platform.velocity.startup.interfaces.IVelocityEnabler;
import com.tjxjnoobie.api.managers.MySQL;
import com.tjxjnoobie.proxy.Events.VelocityLoginEvent;
import com.tjxjnoobie.proxy.Events.VelocityPreLoginEvent;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.slf4j.Logger;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;

import java.lang.reflect.Field;
import java.sql.DriverManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Testcontainers
class MockVelocityLifecycleTest {

    @Container
    private static final MySQLContainer MYSQL = new MySQLContainer("mysql:8.4");

    @BeforeAll
    static void connectDatabase() throws Exception {
        MySQL.connection = DriverManager.getConnection(
                MYSQL.getJdbcUrl(),
                MYSQL.getUsername(),
                MYSQL.getPassword()
        );
    }

    @AfterAll
    static void disconnectDatabase() throws Exception {
        if (MySQL.connection != null) {
            MySQL.connection.close();
            MySQL.connection = null;
        }
    }

    @Test
    void simulatesVelocityStartupLifecycleWithMockedVelocitySurfaces() throws Throwable {
        VelocityMain velocityMain = new VelocityMain();
        IVelocityEnabler velocityEnabler = mock(IVelocityEnabler.class);
        ProxyServer proxyServer = mock(ProxyServer.class);
        EventManager eventManager = mock(EventManager.class);
        Logger logger = mock(Logger.class);
        ProxyInitializeEvent event = new ProxyInitializeEvent();

        velocityMain.velocityEnabler = velocityEnabler;
        setField(velocityMain, "proxyServer", proxyServer);
        setField(velocityMain, "logger", logger);
        when(proxyServer.getEventManager()).thenReturn(eventManager);

        velocityMain.onProxyInitialization(event);

        ArgumentCaptor<String> commandNames = ArgumentCaptor.forClass(String.class);
        InOrder inOrder = inOrder(velocityEnabler, eventManager);

        inOrder.verify(velocityEnabler).onVelocityEnable(same(event));
        inOrder.verify(velocityEnabler, times(8))
                .registerCommand(commandNames.capture(), any(Command.class), same(proxyServer));

        assertEquals(
                List.of("sim", "rank", "ban", "kick", "mute", "warn", "unban", "store"),
                commandNames.getAllValues()
        );

        inOrder.verify(eventManager).register(same(velocityMain), isA(VelocityPreLoginEvent.class));
        inOrder.verify(eventManager).register(same(velocityMain), isA(VelocityLoginEvent.class));
    }

    private static void setField(VelocityMain velocityMain, String fieldName, Object value) throws Exception {
        Field field = VelocityMain.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(velocityMain, value);
    }
}
