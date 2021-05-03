package io.alerium.chocolate.listener;

import com.velocitypowered.api.event.Subscribe;
import io.alerium.chocolate.ChocolatePlugin;
import io.alerium.chocolate.config.Lang;
import io.alerium.chocolate.event.PubSubEvent;
import io.alerium.chocolate.utils.StringUtils;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.Arrays;

public class RedisMessageListener {

    @Subscribe
    public void onPubSub(PubSubEvent event) {
        String message = event.getMessage();
        String[] args = message.split(":");
        if (args.length < 2 || !args[0].equalsIgnoreCase("alert")) return;

        ChocolatePlugin.getInstance().getServer().sendMessage(LegacyComponentSerializer.legacySection().deserialize(StringUtils.getInstance()
                .replacePlaceholders(Lang.COMMAND$ALERT$FORMAT.getString(), "%message%",
                        String.join(":", Arrays.copyOfRange(args, 1, args.length)))));
    }

}
