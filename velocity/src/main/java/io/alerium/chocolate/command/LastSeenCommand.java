package io.alerium.chocolate.command;

import com.velocitypowered.api.command.CommandSource;
import io.alerium.chocolate.command.constructor.ChocolateCommand;
import io.alerium.chocolate.config.Lang;
import io.alerium.chocolate.object.PlayerData;
import io.alerium.chocolate.utils.StringUtils;
import org.apache.commons.lang.time.DurationFormatUtils;

import java.time.Instant;

public class LastSeenCommand extends ChocolateCommand {

    public LastSeenCommand() {
        this.aliases.add("lastseen");
        this.aliases.add("last-seen");
        this.permission = "Chocolate.Command.LastSeen";
        this.usage = "/lastseen <player>";
        this.description = "Get the time of when a player was last seen.";
    }

    @Override
    public void perform(CommandSource commandSource, String[] args) {
        if (args.length == 0) {
            sendUsageMessage(commandSource);
            return;
        }

        PlayerData playerData = this.chocolatePlugin.getCacheManager().getPlayerData(args[0]);
        if (playerData == null || playerData.getLastOnline() == null) {
            sendMessage(commandSource, StringUtils.getInstance().replacePlaceholders(Lang.GENERIC$PLAYER_NOT_FOUND.getString(),
                    "%player%", args[0]));
            return;
        }

        String lastSeen = DurationFormatUtils.formatDurationWords(Instant.now().toEpochMilli() - playerData.getLastOnline(),
                true, true);
        sendMessage(commandSource, StringUtils.getInstance().replacePlaceholders(Lang.COMMAND$LAST_SEEN$FOUND.getString(),
                "%player%", args[0],
                "%seen%", lastSeen));
    }
}
