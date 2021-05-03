package io.alerium.chocolate.command.constructor;

import com.mojang.brigadier.tree.CommandNode;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import io.alerium.chocolate.ChocolatePlugin;
import io.alerium.chocolate.config.Lang;
import io.alerium.chocolate.utils.StringUtils;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class ChocolateCommand implements SimpleCommand, CommandMeta {

    protected final ChocolatePlugin chocolatePlugin = ChocolatePlugin.getInstance();
    public List<String> aliases = new ArrayList<>();
    public String permission, usage, description;

    @Override
    public void execute(Invocation invocation) {
        CommandSource commandSource = invocation.source();
        String[] args = invocation.arguments();
        if (permission != null && !commandSource.hasPermission(permission)) {
            sendMessage(commandSource, Lang.GENERIC$NO_PERMISSIONS.getString());
            return;
        }

        this.perform(commandSource, args);
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return Collections.emptyList();
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return true;
    }

    public abstract void perform(CommandSource commandSource, String[] args);

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public Collection<CommandNode<CommandSource>> getHints() {
        return Collections.emptyList();
    }

    protected void sendUsageMessage(CommandSource commandSource) {
        sendMessage(commandSource, StringUtils.getInstance().replacePlaceholders(Lang.GENERIC$WRONG_USAGE.getString(),
                "%usage%", this.usage != null ? this.usage : "Not found"
        ));
    }

    protected void sendMessage(CommandSource commandSource, String msg) {
        commandSource.sendMessage(LegacyComponentSerializer.legacySection().deserialize(StringUtils.getInstance().colorize(msg)));
    }

}
