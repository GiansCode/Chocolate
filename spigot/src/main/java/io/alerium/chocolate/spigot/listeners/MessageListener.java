package io.alerium.chocolate.spigot.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import io.alerium.chocolate.spigot.ChocolateSpigotPlugin;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@RequiredArgsConstructor
public class MessageListener implements PluginMessageListener {
    
    private final ChocolateSpigotPlugin plugin;
    
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] data) {
        if (!channel.equalsIgnoreCase("Chocolate"))
            return;
        
        ByteArrayDataInput input = ByteStreams.newDataInput(data);
        String subChannel = input.readUTF();
        
        switch (subChannel.toUpperCase()) {
            case "PROXY":
                UUID uuid = UUID.fromString(input.readUTF());
                String proxy = input.readUTF();
                plugin.updatePlayerProxy(uuid, proxy);
                break;
                
            case "PLAYERCOUNT":
                String server = input.readUTF();
                int amount = input.readInt();
                plugin.updateOnlinePlayers(server, amount);
                break;
        }
    }
    
}
