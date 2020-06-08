package io.alerium.chocolate.redis;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.alerium.chocolate.ChocolatePlugin;
import io.alerium.chocolate.objects.PlayerData;
import lombok.RequiredArgsConstructor;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class CacheManager {
    
    private final ChocolatePlugin plugin;
    
    private final Cache<String, Set<String>> onlines = CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build();
    
    private final Cache<String, UUID> uuids = CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build();
    
    private final Cache<UUID, PlayerData> players = CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build();

    /**
     * This method tries to get the online players from the cache, if not cached it gets the data from redis
     * @param proxy The proxy name
     * @return A Set of Strings, null if not found
     */
    public Set<String> getOnlinePlayers(String proxy) {
        Set<String> players = onlines.getIfPresent(proxy.toLowerCase());
        if (players == null) {
            players = plugin.getRedisManager().getOnlinePlayers(proxy.toLowerCase());
            if (players == null)
                return null;
            
            onlines.put(proxy.toLowerCase(), players);
        }
        
        return players;
    }

    /**
     * This method gets a PlayerData of a Player from the Cache, if not cached it gets the data from redis
     * @param name The name of the Player
     * @return The PlayerData, null if not found
     */
    public PlayerData getPlayerData(String name) {
        UUID uuid = getUUID(name);
        if (uuid == null)
            return null;
        
        PlayerData data = players.getIfPresent(uuid);
        if (data == null) {
            data = plugin.getRedisManager().getPlayer(uuid);
            if (data == null)
                return null;
            
            players.put(uuid, data);
        }
        
        return data;
    }

    /**
     * This method gets the UUID of a Player from the Cache, if not cached it gets the data from redis
     * @param name The name of the Player
     * @return The UUID, null if not found
     */
    private UUID getUUID(String name) {
        UUID uuid = uuids.getIfPresent(name);
        if (uuid == null) {
            uuid = plugin.getRedisManager().getPlayerUUID(name);
            if (uuid == null)
                return null;
            
            uuids.put(name.toLowerCase(), uuid);
        }
        
        return uuid;
    }
    
}
