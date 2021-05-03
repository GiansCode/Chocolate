package io.alerium.chocolate.spigot;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.alerium.chocolate.spigot.hooks.PAPIExpansion;
import io.alerium.chocolate.spigot.listeners.MessageListener;
import io.alerium.chocolate.spigot.listeners.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ChocolateSpigotPlugin extends JavaPlugin {
    
    private final Cache<String, Integer> onlinePlayers = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();
    
    private final Map<UUID, String> playersProxy = new HashMap<>();
    
    @Override
    public void onEnable() {
        registerListeners();
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "Chocolate");
        new PAPIExpansion(this).register();
    }

    @Override
    public void onDisable() {
        
    }
    
    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "Chocolate", new MessageListener(this));
    }
    
    public int getOnlinePlayers(Player player, String server) {
        Integer players = onlinePlayers.getIfPresent(server.toLowerCase());
        if (players == null) {
            players = -1;
            onlinePlayers.put(server.toLowerCase(), players);

            ByteArrayDataOutput output = ByteStreams.newDataOutput();
            output.writeUTF("PlayerCount");
            output.writeUTF(server);
            
            player.sendPluginMessage(this, "Chocolate", output.toByteArray());
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

            player.sendPluginMessage(this, "Chocolate", output.toByteArray());
            return null;
        }
        
        return playersProxy.get(player.getUniqueId());
    }
    
    public void updatePlayerProxy(UUID uuid, String proxy) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null || !player.isOnline())
            return;
        
        playersProxy.put(uuid, proxy);
    }
    
    public void removePlayerProxy(UUID uuid) {
        playersProxy.remove(uuid);
    }
    
}
