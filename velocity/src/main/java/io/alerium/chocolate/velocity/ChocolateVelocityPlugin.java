package io.alerium.chocolate.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import io.alerium.chocolate.velocity.commands.*;
import io.alerium.chocolate.velocity.listeners.PlayerListener;
import io.alerium.chocolate.velocity.listeners.PluginMessageListener;
import io.alerium.chocolate.velocity.redis.CacheManager;
import io.alerium.chocolate.velocity.redis.RedisManager;
import io.alerium.chocolate.velocity.utils.Config;
import lombok.Getter;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

@Plugin(id = "chocolate", name = "Chocolate", version = "1.1.0", authors = {"xQuickGlare"})
public class ChocolateVelocityPlugin {

    @Getter private final Logger logger;
    @Getter private final ProxyServer server;
    @Getter private final Path folderPath;

    @Getter private Config config;
    
    @Getter private RedisManager redisManager;
    @Getter private CacheManager cacheManager;
    
    @Inject
    public ChocolateVelocityPlugin(Logger logger, ProxyServer server, @DataDirectory Path folderPath) {
        this.logger = logger;
        this.server = server;
        this.folderPath = folderPath;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
       if (!registerConfigs())
           return;
       
       registerInstances();
       registerListeners();
       registerCommands();
    }
    
    private boolean registerConfigs() {
        try {
            config = new Config(this, folderPath, "config");
            return true;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "An error occurred while loading config", e);
            return false;
        }
    }
    
    private void registerInstances() {
        redisManager = new RedisManager(this);
        redisManager.enable();

        cacheManager = new CacheManager(this);
    }

    private void registerListeners() {
        server.getEventManager().register(new PlayerListener(this), this);
        server.getEventManager().register(new PluginMessageListener(this), this);
    }
    
    private void registerCommands() {
        server.getCommandManager().register(new FindCommand(this), "find");
        server.getCommandManager().register(new GListCommand(this), "glist");
        server.getCommandManager().register(new IPCommand(this), "ip");
        server.getCommandManager().register(new LastSeenCommand(this), "lastseen");
        server.getCommandManager().register(new ServerIDCommand(this), "serverid");
    }
    
}
