package io.alerium.chocolate.velocity.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.ServerPing;
import io.alerium.chocolate.velocity.ChocolateVelocityPlugin;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlayerListener {
    
    private final ChocolateVelocityPlugin plugin;
    
    @Subscribe
    public void onPlayerJoin(PostLoginEvent event) {
        Player player = event.getPlayer();
        plugin.getRedisManager().createPlayer(player.getUniqueId(), player.getUsername(), player.getRemoteAddress().getAddress().getHostAddress());
    }
    
    @Subscribe
    public void onServerConnected(ServerConnectedEvent event) {
        Player player = event.getPlayer();
        plugin.getRedisManager().setPlayerServer(player.getUniqueId(), event.getServer().getServerInfo().getName());
    }
    
    @Subscribe
    public void onPlayerQuit(DisconnectEvent event) {
        Player player = event.getPlayer();
        plugin.getRedisManager().cleanPlayer(player.getUniqueId());
    }
    
    @Subscribe
    public void onProxyPing(ProxyPingEvent event) {
        ServerPing ping = event.getPing();
    
        ping.asBuilder().onlinePlayers(plugin.getRedisManager().getOnlinePlayers());
    }
    
}
