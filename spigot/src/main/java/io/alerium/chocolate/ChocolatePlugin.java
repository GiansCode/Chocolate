package io.alerium.chocolate;

import io.alerium.chocolate.cache.CacheManager;
import io.alerium.chocolate.hook.PAPIExpansion;
import io.alerium.chocolate.listener.MessageListener;
import io.alerium.chocolate.listener.PlayerListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class ChocolatePlugin extends JavaPlugin {

    private static ChocolatePlugin instance;
    private CacheManager cacheManager;

    public static ChocolatePlugin getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public void onEnable() {
        instance = this;

        this.cacheManager = new CacheManager();
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(this, "Chocolate", new MessageListener(this));
        new PAPIExpansion(this).register();
    }
}
