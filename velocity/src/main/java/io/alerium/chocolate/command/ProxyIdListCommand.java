package io.alerium.chocolate.command;

import com.velocitypowered.api.command.CommandSource;
import io.alerium.chocolate.command.constructor.ChocolateCommand;
import io.alerium.chocolate.config.Lang;
import io.alerium.chocolate.utils.StringUtils;

public class ProxyIdListCommand extends ChocolateCommand {

    public ProxyIdListCommand() {
        this.aliases.add("proxies");
        this.aliases.add("serverids");
        this.aliases.add("proxyids");
        this.permission = "Chocolate.Command.ProxyIdList";
        this.usage = "/proxies";
        this.description = "Get a list with the enabled proxies.";
    }

    @Override
    public void perform(CommandSource commandSource, String[] args) {
        sendMessage(commandSource, StringUtils.getInstance().replacePlaceholders(Lang.COMMAND$PROXY_ID_LIST$LIST.getString(),
                "%proxies%", String.join(", ", this.chocolatePlugin.getCacheManager().getProxies())));
    }
}
