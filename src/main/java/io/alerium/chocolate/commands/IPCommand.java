package io.alerium.chocolate.commands;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import io.alerium.chocolate.ChocolatePlugin;
import io.alerium.chocolate.objects.PlayerData;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IPCommand implements Command {

    private final ChocolatePlugin plugin;

    @Override
    public void execute(CommandSource source, String[] args) {
        if (args.length == 0) {
            source.sendMessage(plugin.getConfig().getMessage("ip_usage"));
            return;
        }

        PlayerData data = plugin.getCacheManager().getPlayerData(args[0]);
        if (data == null) {
            source.sendMessage(plugin.getConfig().getMessage("ip_player_not_found"));
            return;
        }

        if (data.getIp() == null) {
            source.sendMessage(plugin.getConfig().getMessage("ip_player_not_online"));
            return;
        }

        source.sendMessage(plugin.getConfig().getMessage("ip_data", "ip", data.getIp()));
    }

}
