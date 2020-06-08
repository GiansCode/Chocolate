package io.alerium.chocolate.commands;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import io.alerium.chocolate.ChocolatePlugin;
import io.alerium.chocolate.objects.PlayerData;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FindCommand implements Command {
    
    private final ChocolatePlugin plugin;
    
    @Override
    public void execute(CommandSource source, String[] args) {
        if (args.length == 0) {
            source.sendMessage(plugin.getConfig().getMessage("find_usage"));
            return;
        }

        PlayerData data = plugin.getCacheManager().getPlayerData(args[0]);
        if (data == null) {
            source.sendMessage(plugin.getConfig().getMessage("find_player_not_found"));
            return;
        }
        
        if (data.getServer() == null) {
            source.sendMessage(plugin.getConfig().getMessage("find_player_not_found"));
            return;
        }
        
        source.sendMessage(plugin.getConfig().getMessage("find_found", "server", data.getServer(), "proxy", data.getProxy()));
    }
    
}
