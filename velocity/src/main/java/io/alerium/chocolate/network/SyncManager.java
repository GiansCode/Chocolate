package io.alerium.chocolate.network;

import com.velocitypowered.api.proxy.Player;
import io.alerium.chocolate.ChocolatePlugin;
import io.alerium.chocolate.listener.PlayerListener;
import io.alerium.chocolate.object.PlayerData;
import lombok.Getter;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.time.Instant;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SyncManager {

    private final RedissonClient redissonClient;
    private final ChocolatePlugin chocolatePlugin;
    private final ScheduledExecutorService executorService;
    private final String proxyId;
    @Getter
    private int onlinePlayers = 0;

    public SyncManager(RedissonClient redissonClient, ChocolatePlugin chocolatePlugin) {
        this.redissonClient = redissonClient;
        this.chocolatePlugin = chocolatePlugin;
        this.proxyId = chocolatePlugin.getRedisManager().getProxyId();

        this.executorService = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r);
            thread.setName("Chocolate | Sync Manager");
            return thread;
        });

        Iterator<UUID> iterator = getOnlinePlayersInProxy(this.proxyId).iterator();
        while (iterator.hasNext()) cleanPlayer(iterator.next());

        this.executorService.scheduleAtFixedRate(this::runSyncTask, 1, 1, TimeUnit.SECONDS);
        this.chocolatePlugin.getServer().getEventManager().register(chocolatePlugin, new PlayerListener(chocolatePlugin));
    }

    private void runSyncTask() {
        AtomicInteger onlinePlayers = new AtomicInteger();
        RMap<String, Long> proxies = redissonClient.getMap("proxies");

        proxies.forEach((proxyName, lastResponse) -> {
            if (Instant.now().toEpochMilli() - lastResponse > TimeUnit.SECONDS.toMillis(5)) return;
            onlinePlayers.addAndGet((int) redissonClient.getMap("proxies:online:count").getOrDefault(proxyName, 0));
        });

        this.onlinePlayers = onlinePlayers.get();
        proxies.put(this.proxyId, Instant.now().toEpochMilli());
        redissonClient.getMap("proxies:online:count").put(this.proxyId, chocolatePlugin.getServer().getPlayerCount());
    }

    /**
     * Create a player his data, mostly used on player join.
     *
     * @param player The player you want to create the data from
     */
    public void createPlayer(Player player) {
        if (!player.getCurrentServer().isPresent()) throw new NullPointerException("Player must be in a server.");

        PlayerData playerData = chocolatePlugin.getCacheManager().getPlayerData(player.getUniqueId());
        if (playerData == null) playerData = new PlayerData(player.getUniqueId());

        playerData.setProxy(proxyId);
        playerData.setIp(player.getRemoteAddress().getAddress().getHostAddress());
        playerData.setServer(player.getCurrentServer().get().getServerInfo().getName());
        playerData.setLastOnline(Instant.now().toEpochMilli());

        redissonClient.getMap("online:playersData").put(player.getUniqueId(), playerData);
    }

    /**
     * Cleanup the data of a player.
     *
     * @param uuid The uuid of the player you want to clean
     */
    public void cleanPlayer(UUID uuid) {
        PlayerData playerData = chocolatePlugin.getCacheManager().getPlayerData(uuid);
        if (playerData == null) return;

        playerData.setLastOnline(Instant.now().toEpochMilli());
        playerData.setProxy(null);
        playerData.setServer(null);

        redissonClient.getMap("online:playersData").put(uuid, playerData);
    }

    /**
     * Change the server the player is on.
     *
     * @param uuid   The UUID of the player where you want to change the server from
     * @param server The server you want to change it to
     */
    public void setServer(UUID uuid, String server) {
        PlayerData playerData = chocolatePlugin.getCacheManager().getPlayerData(uuid);
        if (playerData == null | server == null) return;

        playerData.setServer(server);

        redissonClient.getMap("online:playersData").put(uuid, playerData);
    }

    /**
     * Gets the PlayerData of someone, if not found null.
     *
     * @param uuid The UUID of the player you want to receive the PlayerData from
     * @return PlayerData of the player
     */
    public PlayerData getPlayerData(UUID uuid) {
        RMap<UUID, PlayerData> players = redissonClient.getMap("online:playersData");
        return players.getOrDefault(uuid, null);
    }

    /**
     * Get the online players in a proxy.
     *
     * @param proxy The name of the proxy you want to get the players of
     * @return The online players in that proxy
     */
    public Set<UUID> getOnlinePlayersInProxy(String proxy) {
        Map<UUID, PlayerData> players = redissonClient.getMap("online:playersData");
        return players != null ? players.values().stream()
                .filter(playerData -> playerData.getProxy() != null && playerData.getProxy().equalsIgnoreCase(proxy))
                .map(PlayerData::getUuid)
                .collect(Collectors.toSet()) : null;
    }

    /**
     * Get the online players in a server.
     *
     * @param server The name of the server you want to get the players of
     * @return The online players in that server
     */
    public Set<UUID> getOnlinePlayersInServer(String server) {
        Map<UUID, PlayerData> players = redissonClient.getMap("online:playersData");
        return players != null ? players.values().stream()
                .filter(playerData -> playerData.getServer() != null && playerData.getServer().equalsIgnoreCase(server))
                .map(PlayerData::getUuid)
                .collect(Collectors.toSet()) : null;
    }

    /**
     * Get the id's of the active proxies.
     *
     * @return the id's of the active proxies
     */
    public Set<String> getProxies() {
        Map<String, Long> proxies = redissonClient.getMap("proxies");
        return proxies.keySet();
    }

    /**
     * Gets the online player count of all the proxies combined.
     *
     * @return The online player count
     */
    public int getOnlinePlayerCount() {
        return this.onlinePlayers;
    }

    /**
     * Shutdown the SyncManager
     */
    public void shutdown() {
        Iterator<UUID> iterator = getOnlinePlayersInProxy(this.proxyId).iterator();
        while (iterator.hasNext()) cleanPlayer(iterator.next());

        Map<String, Long> proxies = this.redissonClient.getMap("proxies");
        proxies.remove(this.proxyId);

        this.executorService.shutdown();
    }

}
