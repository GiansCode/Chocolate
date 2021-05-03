package io.alerium.chocolate.command;

import com.velocitypowered.api.command.CommandSource;
import io.alerium.chocolate.command.constructor.ChocolateCommand;
import io.alerium.chocolate.config.Lang;
import io.alerium.chocolate.utils.StringUtils;

import java.util.Set;
import java.util.UUID;

public class GlistCommand extends ChocolateCommand {

    public GlistCommand() {
        this.aliases.add("glist");
        this.aliases.add("network-list");
        this.aliases.add("global-list");
        this.permission = "Chocolate.Command.Glist";
        this.usage = "/glist <proxy>";
        this.description = "Receive the global list.";
    }

    @Override
    public void perform(CommandSource commandSource, String[] args) {
        if (args.length == 0) {
            sendMessage(commandSource, StringUtils.getInstance().replacePlaceholders(Lang.COMMAND$GLIST$GLOBAL.getString(),
                    "%online%", String.valueOf(this.chocolatePlugin.getSyncManager().getOnlinePlayers())));
            return;
        }

        String proxy = args[0];
        Set<UUID> playersInProxy = this.chocolatePlugin.getCacheManager().getOnlinePlayersInProxy(proxy);
        if (playersInProxy == null) {
            sendMessage(commandSource, StringUtils.getInstance().replacePlaceholders(Lang.GENERIC$PROXY_NOT_FOUND.getString(),
                    "%proxy%", args[0]));
            return;
        }

        sendMessage(commandSource, StringUtils.getInstance().replacePlaceholders(Lang.COMMAND$GLIST$PROXY.getString(),
                "%proxy%", args[0]));
    }
}
