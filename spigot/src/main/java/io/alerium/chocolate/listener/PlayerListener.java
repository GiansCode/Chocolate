package io.alerium.chocolate.listener;

import io.alerium.chocolate.ChocolatePlugin;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class PlayerListener implements Listener {

    private final ChocolatePlugin plugin;

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getCacheManager().removePlayerProxy(event.getPlayer().getUniqueId());
    }

}