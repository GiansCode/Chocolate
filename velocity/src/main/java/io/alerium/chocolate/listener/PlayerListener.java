package io.alerium.chocolate.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import io.alerium.chocolate.ChocolatePlugin;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlayerListener {

    private final ChocolatePlugin chocolatePlugin;

    @Subscribe
    public void onPing(ProxyPingEvent event) {
        event.setPing(event.getPing().asBuilder().onlinePlayers(chocolatePlugin.getSyncManager().getOnlinePlayers()).build());
    }

    @Subscribe
    public void onServerPostConnect(ServerPostConnectEvent event) {
        if (!event.getPlayer().getCurrentServer().isPresent() || event.getPreviousServer() != null) return;
        this.chocolatePlugin.getSyncManager().createPlayer(event.getPlayer());
    }

    @Subscribe
    public void onServerConnected(ServerConnectedEvent event) {
        if (event.getPlayer() == null) return;
        this.chocolatePlugin.getSyncManager().setServer(event.getPlayer().getUniqueId(), event.getServer().getServerInfo().getName());
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        this.chocolatePlugin.getSyncManager().cleanPlayer(event.getPlayer().getUniqueId());
    }

}
