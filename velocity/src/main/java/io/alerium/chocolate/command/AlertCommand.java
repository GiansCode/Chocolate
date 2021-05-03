package io.alerium.chocolate.command;

import com.velocitypowered.api.command.CommandSource;
import io.alerium.chocolate.command.constructor.ChocolateCommand;

public class AlertCommand extends ChocolateCommand {

    public AlertCommand() {
        this.aliases.add("alert");
        this.aliases.add("global-broadcast");
        this.aliases.add("gbroadcast");
        this.permission = "Chocolate.Command.Alert";
        this.usage = "/alert <message>";
        this.description = "Send a broadcast over all the proxies.";
    }

    @Override
    public void perform(CommandSource commandSource, String[] args) {
        if (args.length < 1) {
            sendUsageMessage(commandSource);
            return;
        }

        String msg = String.join(" ", args);
        this.chocolatePlugin.getRedisManager().getRedissonClient().getTopic("chocolate").publish("alert:" + msg);
    }
}
