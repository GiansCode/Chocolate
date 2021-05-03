package io.alerium.chocolate.command;

import com.velocitypowered.api.command.CommandSource;
import io.alerium.chocolate.command.constructor.ChocolateCommand;
import io.alerium.chocolate.config.Lang;
import io.alerium.chocolate.object.PlayerData;
import io.alerium.chocolate.utils.StringUtils;

public class IPCommand extends ChocolateCommand {

    public IPCommand() {
        this.aliases.add("ip");
        this.aliases.add("find-ip");
        this.aliases.add("ip-address");
        this.permission = "Chocolate.Command.IP";
        this.usage = "/ip <player>";
        this.description = "Get the ip of a player.";
    }

    @Override
    public void perform(CommandSource commandSource, String[] args) {
        if (args.length == 0) {
            sendUsageMessage(commandSource);
            return;
        }

        PlayerData playerData = this.chocolatePlugin.getCacheManager().getPlayerData(args[0]);
        if (playerData == null || playerData.getIp() == null) {
            sendMessage(commandSource, StringUtils.getInstance().replacePlaceholders(Lang.GENERIC$PLAYER_NOT_FOUND.getString(),
                    "%player%", args[0]));
            return;
        }

        sendMessage(commandSource, StringUtils.getInstance().replacePlaceholders(Lang.COMMAND$IP$FOUND.getString(),
                "%player%", args[0],
                "%ip%", playerData.getIp()));
    }
}
