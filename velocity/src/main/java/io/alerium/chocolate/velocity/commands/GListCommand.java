package io.alerium.chocolate.velocity.commands;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import io.alerium.chocolate.velocity.ChocolateVelocityPlugin;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
public class GListCommand implements Command {
    
    private final ChocolateVelocityPlugin plugin;
    
    @Override
    public void execute(CommandSource source, String[] args) {
        if (args.length == 0) {
            source.sendMessage(plugin.getConfig().getMessage("glist_global", "players", Integer.toString(plugin.getRedisManager().getOnlinePlayers())));
            return;
        }
        
        String proxy = args[0];
        Set<String> players = plugin.getCacheManager().getOnlinePlayers(proxy);
        if (players == null) {
            source.sendMessage(plugin.getConfig().getMessage("glist_no_proxy"));
            return;
        }
        
        source.sendMessage(plugin.getConfig().getMessage("glist_proxy", "proxy", proxy, "players", Integer.toString(players.size())));
    }
    
}
