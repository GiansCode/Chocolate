package io.alerium.chocolate.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.TabCompleteEvent;
import io.alerium.chocolate.ChocolatePlugin;
import io.alerium.chocolate.network.CacheManager;

import java.util.stream.Collectors;

public class TabCompleteListener {

    private final CacheManager cacheManager;

    public TabCompleteListener(ChocolatePlugin chocolatePlugin) {
        this.cacheManager = chocolatePlugin.getCacheManager();
    }

    @Subscribe
    public void onTabComplete(TabCompleteEvent e) {
        if (e.getSuggestions() != null && !e.getSuggestions().isEmpty()) return;

        String[] args = e.getPartialMessage().split(" ");
        if (args.length == 0) {
            e.getSuggestions().addAll(cacheManager.getOnlinePlayersNames());
            return;
        }

        String lastArg = args[args.length - 1].toLowerCase();
        if (args.length < 2) {
            e.getSuggestions().addAll(cacheManager.getOnlinePlayersNames()
                    .stream()
                    .filter(m -> m.toLowerCase().startsWith(lastArg))
                    .collect(Collectors.toList()));
        }
    }

}
