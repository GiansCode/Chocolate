package io.alerium.chocolate.velocity.redis;

import com.google.gson.JsonObject;
import io.alerium.chocolate.velocity.ChocolateVelocityPlugin;
import io.alerium.chocolate.velocity.objects.PlayerData;
import io.alerium.chocolate.velocity.objects.PubSubListener;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.*;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class RedisManager {
    
    private final ChocolateVelocityPlugin plugin;
    
    private PubSubListener psListener;
    
    private String redisPassword;
    @Getter private String proxyName;
    @Getter private int onlinePlayers;
    
    private JedisPool pool;

    /**
     * This method enables the RedisManager
     */
    public void enable() {
        JsonObject redisConfig = plugin.getConfig().getObject().getAsJsonObject("redis");
        redisPassword = redisConfig.has("password") ? redisConfig.get("password").getAsString() : null;
        pool = new JedisPool(new JedisPoolConfig(), redisConfig.get("hostname").getAsString(), redisConfig.get("port").getAsInt());

        psListener = new PubSubListener(plugin);
        try (Jedis jedis = getConnection()) {
            jedis.subscribe(psListener, "chocolate");
        }
        
        proxyName = plugin.getConfig().getObject().get("proxyName").getAsString();
        
        plugin.getServer().getScheduler().buildTask(plugin, () -> {
            int onlines = 0;
            
            try (Jedis jedis = getConnection()) {
                Map<String, String> proxies = jedis.hgetAll("proxies");
                for (String proxy : proxies.keySet()) {
                    if (System.currentTimeMillis() - Long.parseLong(proxies.get(proxy)) > TimeUnit.SECONDS.toMillis(5)) {
                        plugin.getLogger().severe("No heartbeat from " + proxy + " in 5 seconds, ignoring players.");
                        continue;
                    }
                    
                    onlines += jedis.scard("proxy:" + proxy + ":onlines");
                }
                
                jedis.hset("proxies", proxyName, Long.toString(System.currentTimeMillis()));
            }
            
            onlinePlayers = onlines;
        }).repeat(1, TimeUnit.SECONDS);
    }

    /**
     * This method creates a new player and sets all the info
     * @param player The Player UUID
     * @param name The Player name
     * @param ip The Player IP
     */
    public void createPlayer(UUID player, String name, String ip) {
        Map<String, String> data = new HashMap<>();
        data.put("ip", ip);
        data.put("lastOnline", "0");
        data.put("proxy", proxyName);
        
        try (Jedis jedis = getConnection()) {
            jedis.sadd("proxy:" + proxyName + ":onlines", player.toString());
            jedis.hmset("player:" + player, data);
            
            jedis.set("playeruuid:" + name.toLowerCase(), player.toString());
        }
    }

    /**
     * This method sets the server where a Player is
     * @param player The Player's UUID
     * @param server The server
     */
    public void setPlayerServer(UUID player, String server) {
        try (Jedis jedis = getConnection()) {
            jedis.hset("player:" + player, "server", server);
        }
    }

    /**
     * This method cleans all the info of a Player
     * @param player The UUID of the Player
     */
    public void cleanPlayer(UUID player) {
        try (Jedis jedis = getConnection()) {
            jedis.srem("proxy:" + proxyName + ":onlines", player.toString());
            jedis.hdel("player:" + player, "server", "ip", "proxy");
        
            jedis.hset("player:" + player, "lastOnline", Long.toString(System.currentTimeMillis()));
        }
    }

    /**
     * This method gets the UUID of a Player
     * @param player The Player name
     * @return The UUID, null if not found
     */
    public UUID getPlayerUUID(String player) {
        try (Jedis jedis = getConnection()) {
            String s = jedis.get("playeruuid:" + player.toLowerCase());
            if (s == null)
                return null;
            
            return UUID.fromString(s);
        }
    }

    /**
     * This method gets the PlayerData of a Player
     * @param uuid The UUID of the Player
     * @return The PlayerData, null if not found
     */
    public PlayerData getPlayer(UUID uuid) {
        try (Jedis jedis = getConnection()) {
            Map<String, String> data = jedis.hgetAll("player:" + uuid);
            if (data == null || data.isEmpty())
                return null;
            
            return new PlayerData(
                    uuid,
                    Long.parseLong(data.get("lastOnline")),
                    data.getOrDefault("ip", null),
                    data.getOrDefault("proxy", null),
                    data.getOrDefault("server", null)
            );
        }
    }

    /**
     * This method gets all the online players in a proxy
     * @param proxy The proxy name
     * @return A Set of Strings with all the Players, null if proxy not found
     */
    public Set<String> getOnlinePlayers(String proxy) {
        try (Jedis jedis = getConnection()) {
            return jedis.smembers("proxy:" + proxy + ":onlines");
        }
    }

    /**
     * This method gets the online players in a specified server
     * @param server The server name
     * @return A list of the players in the server (UUID)
     */
    public Set<UUID> getOnlinePlayersInServer(String server) {
        Set<UUID> onlines = new HashSet<>();
        
        try (Jedis jedis = getConnection()) {
            Map<String, String> proxies = jedis.hgetAll("proxies");
            for (String proxy : proxies.keySet()) {
                Set<String> players = plugin.getCacheManager().getOnlinePlayers(proxy);
                for (String player : players) {
                    UUID uuid = UUID.fromString(player);
                    String playerServer = jedis.hget("player:" + uuid, "server");
                    if (playerServer.equalsIgnoreCase(server))
                        onlines.add(uuid);
                }
            }
        }
        
        return onlines;
    }
    
    /**
     * This method register a channel to be used in the PubSubEvent
     * @param channels The channels to register
     */
    public void registerChannel(String... channels) {
        psListener.subscribe(channels);
    }

    /**
     * This method unregister a channel for the PubSubEvent
     * @param channels The channels to unregister
     */
    public void unregisterChannel(String... channels) {
        psListener.unsubscribe(channels);
    }
    
    private Jedis getConnection() {
        Jedis jedis = pool.getResource();
        if (redisPassword != null && !redisPassword.isEmpty())
            jedis.auth(redisPassword);
        return jedis;
    }
    
}
