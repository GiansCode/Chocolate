package io.alerium.chocolate.velocity.commands;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import io.alerium.chocolate.velocity.ChocolateVelocityPlugin;
import io.alerium.chocolate.velocity.objects.PlayerData;
import lombok.RequiredArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;

@RequiredArgsConstructor
public class LastSeenCommand implements Command {

    private final ChocolateVelocityPlugin plugin;

    @Override
    public void execute(CommandSource source, String[] args) {
        if (args.length == 0) {
            source.sendMessage(plugin.getConfig().getMessage("last_seen_usage"));
            return;
        }

        PlayerData data = plugin.getCacheManager().getPlayerData(args[0]);
        if (data == null) {
            source.sendMessage(plugin.getConfig().getMessage("last_seen_player_not_found"));
            return;
        }
        
        if (data.getLastOnline() == 0) {
            source.sendMessage(plugin.getConfig().getMessage("last_seen_already_online"));
            return;
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(data.getLastOnline());
        source.sendMessage(plugin.getConfig().getMessage("last_seen_time", "time", format.format(date)));
    }
    
}
