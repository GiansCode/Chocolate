package io.alerium.chocolate.commands;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import io.alerium.chocolate.ChocolatePlugin;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServerIDCommand implements Command {
    
    private final ChocolatePlugin plugin;
    
    @Override
    public void execute(CommandSource source, String[] strings) {
        source.sendMessage(plugin.getConfig().getMessage("server_id", "proxy", plugin.getRedisManager().getProxyName()));
    }
    
}
