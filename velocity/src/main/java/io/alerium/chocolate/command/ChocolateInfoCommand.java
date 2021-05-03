package io.alerium.chocolate.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginDescription;
import io.alerium.chocolate.command.constructor.ChocolateCommand;
import io.alerium.chocolate.config.Lang;
import io.alerium.chocolate.utils.StringUtils;

import java.util.Optional;

public class ChocolateInfoCommand extends ChocolateCommand {

    public ChocolateInfoCommand() {
        this.aliases.add("chocolate");
        this.permission = "Chocolate.Command.Chocolate";
        this.usage = "/chocolate";
        this.description = "Sends the info message & command list.";
    }

    @Override
    public void perform(CommandSource commandSource, String[] args) {
        StringBuilder commandBuilder = new StringBuilder();

        for (ChocolateCommand chocolateCommand : this.chocolatePlugin.getCommandRegister().getChocolateCommands()) {
            commandBuilder.append(StringUtils.getInstance().replacePlaceholders(Lang.COMMAND$CHOCOLATE$COMMAND_FORMAT.getString(),
                    "%usage%", chocolateCommand.usage, "%description%", chocolateCommand.description)).append("\n");
        }

        Optional<PluginContainer> optional = chocolatePlugin.getServer().getPluginManager().getPlugin("chocolate");
        if (!optional.isPresent()) return;
        PluginDescription pluginDescription = optional.get().getDescription();

        sendMessage(commandSource, StringUtils.getInstance().replacePlaceholders(Lang.COMMAND$CHOCOLATE$MSG.getString(),
                "%authors%", String.join(", ", pluginDescription.getAuthors()),
                "%commands%", commandBuilder.toString()));
    }
}
