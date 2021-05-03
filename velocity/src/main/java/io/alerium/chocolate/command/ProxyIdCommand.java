package io.alerium.chocolate.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import io.alerium.chocolate.command.constructor.ChocolateCommand;
import io.alerium.chocolate.config.Lang;
import io.alerium.chocolate.object.PlayerData;
import io.alerium.chocolate.utils.StringUtils;

public class ProxyIdCommand extends ChocolateCommand {

    public ProxyIdCommand() {
        this.aliases.add("proxy");
        this.aliases.add("serverid");
        this.aliases.add("proxyid");
        this.permission = "Chocolate.Command.Proxy";
        this.usage = "/proxy";
        this.description = "Get the id of the proxy you are on.";
    }

    @Override
    public void perform(CommandSource commandSource, String[] args) {
        if (!(commandSource instanceof Player)) {
            sendMessage(commandSource, StringUtils.getInstance().colorize(Lang.GENERIC$MUST_BE_A_PLAYER.getString()));
            return;
        }
        PlayerData playerData = this.chocolatePlugin.getCacheManager().getPlayerData(((Player) commandSource).getUniqueId());
        sendMessage(commandSource, StringUtils.getInstance().replacePlaceholders(Lang.COMMAND$PROXY_ID$ID.getString(),
                "%proxyId%", playerData.getProxy()));
    }
}
