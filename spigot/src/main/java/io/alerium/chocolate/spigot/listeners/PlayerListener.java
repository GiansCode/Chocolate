package io.alerium.chocolate.spigot.listeners;

import io.alerium.chocolate.spigot.ChocolateSpigotPlugin;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class PlayerListener implements Listener {
    
    private final ChocolateSpigotPlugin plugin;
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.removePlayerProxy(event.getPlayer().getUniqueId());
    }
    
}
