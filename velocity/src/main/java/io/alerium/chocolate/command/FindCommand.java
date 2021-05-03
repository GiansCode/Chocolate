package io.alerium.chocolate.command;

import com.velocitypowered.api.command.CommandSource;
import io.alerium.chocolate.command.constructor.ChocolateCommand;
import io.alerium.chocolate.config.Lang;
import io.alerium.chocolate.object.PlayerData;
import io.alerium.chocolate.utils.StringUtils;

public class FindCommand extends ChocolateCommand {

    public FindCommand() {
        this.aliases.add("find");
        this.aliases.add("find-player");
        this.aliases.add("search-player");
        this.permission = "Chocolate.Command.Find";
        this.usage = "/find <player>";
        this.description = "Find out where a player is.";
    }

    @Override
    public void perform(CommandSource commandSource, String[] args) {
        if (args.length == 0) {
            sendUsageMessage(commandSource);
            return;
        }

        PlayerData playerData = this.chocolatePlugin.getCacheManager().getPlayerData(args[0]);
        if (playerData == null || playerData.getServer() == null || playerData.getProxy() == null) {
            sendMessage(commandSource, StringUtils.getInstance().replacePlaceholders(Lang.GENERIC$PLAYER_NOT_FOUND.getString(),
                    "%player%", args[0]));
            return;
        }

        sendMessage(commandSource, StringUtils.getInstance().replacePlaceholders(Lang.COMMAND$FIND$FOUND.getString(),
                "%player%", args[0],
                "%server%", playerData.getServer(),
                "%proxy%", playerData.getProxy()));
    }
}
