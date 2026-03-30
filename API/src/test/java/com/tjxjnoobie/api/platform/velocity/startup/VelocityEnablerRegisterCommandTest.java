package com.tjxjnoobie.api.platform.velocity.startup;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ProxyServer;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VelocityEnablerRegisterCommandTest {

    @Test
    void registersVelocityCommandWithAliasesThroughMockedProxyServer() {
        ProxyServer proxyServer = mock(ProxyServer.class);
        CommandManager commandManager = mock(CommandManager.class);
        CommandMeta.Builder metaBuilder = mock(CommandMeta.Builder.class);
        CommandMeta commandMeta = mock(CommandMeta.class);
        SimpleCommand command = invocation -> {
        };

        when(proxyServer.getCommandManager()).thenReturn(commandManager);
        when(commandManager.metaBuilder("rank")).thenReturn(metaBuilder);
        when(metaBuilder.aliases("r", "rk")).thenReturn(metaBuilder);
        when(metaBuilder.build()).thenReturn(commandMeta);

        new VelocityEnabler().registerCommand("rank", command, proxyServer, "r", "rk");

        verify(commandManager).metaBuilder("rank");
        verify(metaBuilder).aliases("r", "rk");
        verify(metaBuilder).build();
        verify(commandManager).register(same(commandMeta), same(command));
    }
}
