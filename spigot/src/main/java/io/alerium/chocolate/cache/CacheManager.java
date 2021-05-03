package io.alerium.chocolate.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.alerium.chocolate.ChocolatePlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CacheManager {

    private final Cache<String, Integer> onlinePlayers = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();
    private final Map<UUID, String> playersProxy = new HashMap<>();

    public int getOnlinePlayers(Player player, String server) {
        Integer players = onlinePlayers.getIfPresent(server.toLowerCase());
        if (players == null) {
            players = -1;
            onlinePlayers.put(server.toLowerCase(), players);

            ByteArrayDataOutput output = ByteStreams.newDataOutput();
            output.writeUTF("PlayerCount");
            output.writeUTF(server);

            player.sendPluginMessage(ChocolatePlugin.getInstance(), "Chocolate", output.toByteArray());
        }

        return players;
    }

    public void updateOnlinePlayers(String server, int amount) {
        onlinePlayers.put(server.toLowerCase(), amount);
    }

    public String getPlayerProxy(Player player) {
        if (!playersProxy.containsKey(player.getUniqueId())) {
            playersProxy.put(player.getUniqueId(), null);

            ByteArrayDataOutput output = ByteStreams.newDataOutput();
            output.writeUTF("Proxy");
            output.writeUTF(player.getUniqueId().toString());

            player.sendPluginMessage(ChocolatePlugin.getInstance(), "Chocolate", output.toByteArray());
            return null;
        }

        return playersProxy.get(player.getUniqueId());
    }

    public void updatePlayerProxy(UUID uuid, String proxy) {
        Player player = Bukkit.getServer().getPlayer(uuid);
        if (player == null || !player.isOnline())
            return;

        playersProxy.put(uuid, proxy);
    }

    public void removePlayerProxy(UUID uuid) {
        playersProxy.remove(uuid);
    }

}
