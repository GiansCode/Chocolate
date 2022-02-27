package io.alerium.chocolate;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import eu.vertcode.vertconfig.VertConfigs;
import eu.vertcode.vertconfig.object.VertConfig;
import io.alerium.chocolate.command.constructor.CommandRegister;
import io.alerium.chocolate.config.Lang;
import io.alerium.chocolate.config.loader.EnumConfigLoader;
import io.alerium.chocolate.listener.PluginMessageListener;
import io.alerium.chocolate.listener.RedisMessageListener;
import io.alerium.chocolate.listener.StartingListener;
import io.alerium.chocolate.listener.TabCompleteListener;
import io.alerium.chocolate.network.CacheManager;
import io.alerium.chocolate.network.SyncManager;
import io.alerium.chocolate.redis.RedisManager;
import io.alerium.chocolate.utils.StringUtils;
import lombok.Getter;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Path;

@Plugin(
        id = "chocolate",
        name = "Chocolate",
        version = "2.0.0",
        authors = {"xQuickGlare", "VertCode"}
)
@Getter
public class ChocolatePlugin {

    private static ChocolatePlugin instance;
    private final Logger logger;
    private final ProxyServer server;
    private final File dataFolder;
    private VertConfig config;
    private RedisManager redisManager;
    private boolean successfullyStarted = false;
    private SyncManager syncManager;
    private CacheManager cacheManager;
    private CommandRegister commandRegister;
    private EnumConfigLoader langLoader;

    @Inject
    public ChocolatePlugin(Logger logger, ProxyServer server, @DataDirectory Path folderPath) {
        this.logger = logger;
        this.server = server;
        this.dataFolder = folderPath.toFile();
        instance = this;
        if (!dataFolder.exists()) dataFolder.mkdir();
    }

    public static ChocolatePlugin getInstance() {
        return instance;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        this.config = VertConfigs.getInstance().getConfig(new File(this.dataFolder, "config.json"),
                this.getClass().getClassLoader().getResourceAsStream("config.json"));
        this.langLoader = new EnumConfigLoader(new File(this.getDataFolder(), "lang.json"), Lang.class);

        this.redisManager = new RedisManager();
        if (!this.redisManager.initialize()) return;

        this.loadInstances();

        this.loadListeners();

        this.commandRegister = new CommandRegister(this);
        this.commandRegister.initialize();

        this.successfullyStarted = true;
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (!this.successfullyStarted) return;
        this.redisManager.shutdown();
    }

    private void loadListeners() {
        this.server.getEventManager().register(this, new StartingListener());
        this.server.getEventManager().register(this, new RedisMessageListener());
        this.server.getEventManager().register(this, new TabCompleteListener(this));
        this.server.getEventManager().register(this, new PluginMessageListener(this));
    }

    private void loadInstances() {
        this.syncManager = new SyncManager(this.redisManager.getRedissonClient(), this);
        this.cacheManager = new CacheManager(this.syncManager);
        this.syncManager.cleanPlayers();
    }

    public void logToConsole(String msg) {
        this.server.getConsoleCommandSource().sendMessage(LegacyComponentSerializer.legacySection().deserialize(StringUtils.getInstance()
                .colorize(msg)));
    }

    public void logToConsole(String msg, Exception ex) {
        this.logger.error(msg, ex);
    }
}
