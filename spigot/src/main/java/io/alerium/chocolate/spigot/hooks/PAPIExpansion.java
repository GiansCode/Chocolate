package io.alerium.chocolate.spigot.hooks;

import io.alerium.chocolate.spigot.ChocolateSpigotPlugin;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class PAPIExpansion extends PlaceholderExpansion {

    private final ChocolateSpigotPlugin plugin;
    
    public @NotNull String getIdentifier() {
        return "chocolate";
    }

    public @NotNull String getAuthor() {
        return "xQuickGlare";
    }

    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (params.equalsIgnoreCase("total_count")) {
            int amount = plugin.getOnlinePlayers(player, "ALL");
            if (amount == -1)
                return "";
            return Integer.toString(1);
        }
        
        if (params.equalsIgnoreCase("player_proxy")) {
            String proxy = plugin.getPlayerProxy(player);
            if (proxy == null)
                return "";
            return proxy;
        }
        
        if (params.startsWith("server_")) {
            String server = params.split("_")[1];
            int amount = plugin.getOnlinePlayers(player, server);
            if (amount == -1)
                return "";
            return Integer.toString(amount);
        }
        return "";
    }
}
