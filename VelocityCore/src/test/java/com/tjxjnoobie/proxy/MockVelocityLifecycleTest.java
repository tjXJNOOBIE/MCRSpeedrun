package com.tjxjnoobie.proxy;

import com.tjxjnoobie.api.platform.velocity.startup.interfaces.IVelocityEnabler;
import com.tjxjnoobie.proxy.Events.VelocityLoginEvent;
import com.tjxjnoobie.proxy.Events.VelocityPreLoginEvent;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import java.lang.reflect.Field;
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

class MockVelocityLifecycleTest {

    @Test
    void simulatesVelocityStartupLifecycleWithMockedVelocitySurfaces() throws Throwable {
        VelocityMain velocityMain = new VelocityMain();
        IVelocityEnabler velocityEnabler = mock(IVelocityEnabler.class);
        ProxyServer proxyServer = mock(ProxyServer.class);
        EventManager eventManager = mock(EventManager.class);
        ProxyInitializeEvent event = new ProxyInitializeEvent();

        velocityMain.velocityEnabler = velocityEnabler;
        setProxyServer(velocityMain, proxyServer);
        when(proxyServer.getEventManager()).thenReturn(eventManager);

        velocityMain.onProxyInitialization(event);

        ArgumentCaptor<String> commandNames = ArgumentCaptor.forClass(String.class);
        InOrder inOrder = inOrder(velocityEnabler, eventManager);

        inOrder.verify(velocityEnabler).onVelocityEnable(same(event));
        inOrder.verify(velocityEnabler, times(7))
                .registerCommand(commandNames.capture(), any(Command.class), same(proxyServer));

        assertEquals(List.of("sim", "rank", "ban", "kick", "mute", "warn", "unban"), commandNames.getAllValues());

        inOrder.verify(eventManager).register(same(velocityMain), isA(VelocityPreLoginEvent.class));
        inOrder.verify(eventManager).register(same(velocityMain), isA(VelocityLoginEvent.class));
    }

    private static void setProxyServer(VelocityMain velocityMain, ProxyServer proxyServer) throws Exception {
        Field proxyServerField = VelocityMain.class.getDeclaredField("proxyServer");
        proxyServerField.setAccessible(true);
        proxyServerField.set(velocityMain, proxyServer);
    }
}
