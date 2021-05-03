package io.alerium.chocolate.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import io.alerium.chocolate.ChocolatePlugin;
import io.alerium.chocolate.config.Lang;
import io.alerium.chocolate.utils.StringUtils;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class StartingListener {

    @Subscribe
    public void onPlayerConnect(PreLoginEvent e) {
        if (ChocolatePlugin.getInstance().isSuccessfullyStarted()) return;
        e.setResult(PreLoginEvent.PreLoginComponentResult.denied(LegacyComponentSerializer.legacySection()
                .deserialize(StringUtils.getInstance().colorize(Lang.STARTING$JOIN.getString()))));
    }

}
