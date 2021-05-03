package io.alerium.chocolate.network;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.velocitypowered.api.util.UuidUtils;
import io.alerium.chocolate.object.PlayerData;
import io.alerium.chocolate.utils.TemporarySet;
import lombok.RequiredArgsConstructor;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CacheManager {

    private final SyncManager syncManager;
    private final Cache<String, Set<UUID>> onlinesPerProxy = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES).build();
    private final Cache<String, Set<UUID>> onlinesPerServer = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES).build();
    private final Cache<String, UUID> uuidCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES).build();
    private final Cache<UUID, String> nameCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES).build();
    private final Cache<UUID, PlayerData> playerCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES).build();
    private final Set<String> proxies = new TemporarySet<>(1000, TimeUnit.MINUTES.toMillis(1));

    /**
     * Gets the PlayerData of someone, if not found null.
     *
     * @param uuid The UUID of the player you want to receive the PlayerData from
     * @return PlayerData of the player
     */
    public PlayerData getPlayerData(UUID uuid) {
        if (this.playerCache.asMap().containsKey(uuid)) return this.playerCache.getIfPresent(uuid);

        PlayerData playerData = syncManager.getPlayerData(uuid);
        if (playerData != null) this.playerCache.put(uuid, playerData);

        return playerData;
    }

    /**
     * Gets the PlayerData of someone, if not found null.
     *
     * @param name The name of the player you want to receive the PlayerData from
     * @return PlayerData of the player
     */
    public PlayerData getPlayerData(String name) {
        UUID uuid = getPlayerUUID(name);
        return uuid == null ? null : getPlayerData(uuid);
    }

    /**
     * Get the online players in a proxy.
     *
     * @param proxy The name of the proxy you want to get the players of
     * @return The online players in that proxy
     */
    public Set<UUID> getOnlinePlayersInProxy(String proxy) {
        if (this.onlinesPerProxy.asMap().containsKey(proxy)) return this.onlinesPerProxy.getIfPresent(proxy);

        Set<UUID> onlinePlayers = this.syncManager.getOnlinePlayersInProxy(proxy);
        if (onlinePlayers == null) return null;

        this.onlinesPerProxy.put(proxy, onlinePlayers);
        return onlinePlayers;
    }

    /**
     * Get the online players in a server.
     *
     * @param server The name of the server you want to get the players of
     * @return The online players in that server
     */
    public Set<UUID> getOnlinePlayersInServer(String server) {
        if (this.onlinesPerServer.asMap().containsKey(server)) return this.onlinesPerServer.getIfPresent(server);

        Set<UUID> onlinePlayers = this.syncManager.getOnlinePlayersInServer(server);
        if (onlinePlayers == null) return null;

        this.onlinesPerServer.put(server, onlinePlayers);
        return onlinePlayers;
    }

    /**
     * Get the id's of the active proxies.
     *
     * @return the id's of the active proxies
     */
    public Set<String> getProxies() {
        if (!this.proxies.isEmpty()) return proxies;

        Set<String> proxies = this.syncManager.getProxies();
        this.proxies.addAll(proxies);

        return proxies;
    }

    /**
     * Get a player his UUID by his name.
     *
     * @param name The name of the player you want to get the UUID from
     * @return The UUID of the player
     */
    public UUID getPlayerUUID(String name) {
        if (this.uuidCache.asMap().containsKey(name)) return this.uuidCache.getIfPresent(name);

        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(false);
            connection.connect();

            Map<String, String> map = new Gson().fromJson(new InputStreamReader(connection.getInputStream()),
                    new TypeToken<Map<String, String>>() {
                    }.getType());

            connection.disconnect();
            UUID uuid = UuidUtils.fromUndashed(map.get("id"));
            this.uuidCache.put(name, uuid);
            return uuid;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Get a player his Name by his UUID.
     *
     * @param uuid The uuid of the player you want to get the name from
     * @return The name of the player
     */
    public String getPlayerName(UUID uuid) {
        if (this.nameCache.asMap().containsKey(uuid)) return this.nameCache.getIfPresent(uuid);
        try {
            URL url = new URL("https://api.mojang.com/user/profiles/" + uuid + "/names");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(false);
            connection.connect();

            JsonArray array = new Gson().fromJson(new InputStreamReader(connection.getInputStream()), JsonArray.class);
            connection.disconnect();

            if (array == null || array.size() < 1) return null;

            String name = array.get(array.size() - 1).getAsJsonObject().get("name").getAsString();
            this.nameCache.put(uuid, name);
            return name;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Gets a list with all the names of the players online.
     *
     * @return A list with the name of every player online
     */
    public Set<String> getOnlinePlayersNames() {
        Set<UUID> str = null;
        Iterator<String> proxies = this.getProxies().iterator();

        while (proxies.hasNext()) {
            String proxy = proxies.next();
            Set<UUID> players = this.getOnlinePlayersInProxy(proxy);
            if (str == null) str = players;
            else str.addAll(players);
        }
        return str == null ? Sets.newHashSet() : str.stream().map(this::getPlayerName).collect(Collectors.toSet());
    }
}
