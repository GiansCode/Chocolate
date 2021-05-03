package io.alerium.chocolate;

import io.alerium.chocolate.cache.CacheManager;
import io.alerium.chocolate.listener.MessageListener;
import io.alerium.chocolate.listener.PlayerListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        printLogo();

        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "Loading..."));
        this.cacheManager = new CacheManager();
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(this, "Chocolate", new MessageListener(this));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "Loaded."));
    }

    private void printLogo() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\n" +
                "_________ .__                        .__          __          \n" +
                "\\_   ___ \\|  |__   ____   ____  ____ |  | _____ _/  |_  ____  \n" +
                "/    \\  \\/|  |  \\ /  _ \\_/ ___\\/  _ \\|  | \\__  \\\\   __\\/ __ \\ \n" +
                "\\     \\___|   Y  (  <_> )  \\__(  <_> )  |__/ __ \\|  | \\  ___/ \n" +
                " \\______  /___|  /\\____/ \\___  >____/|____(____  /__|  \\___  >\n" +
                "        \\/     \\/            \\/                \\/          \\/ "));
    }
}
