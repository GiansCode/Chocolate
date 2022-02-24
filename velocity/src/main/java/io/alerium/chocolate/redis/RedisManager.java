package io.alerium.chocolate.redis;

import io.alerium.chocolate.ChocolatePlugin;
import io.alerium.chocolate.event.PubSubEvent;
import lombok.Getter;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class RedisManager {

    private final PubSubEvent.Handler pubSubEvent = new PubSubEvent.Handler();
    private final ChocolatePlugin chocolatePlugin = ChocolatePlugin.getInstance();
    private final File file = new File(ChocolatePlugin.getInstance().getDataFolder(), "redis.yml");
    private Config redisConfig;
    @Getter
    private RedissonClient redissonClient;
    @Getter
    private String proxyId;

    public boolean initialize() {
        if (!file.exists()) loadDefaultConfig();
        try {
            this.redisConfig = Config.fromYAML(this.file);
            this.redisConfig.setCodec(JsonJacksonCodec.INSTANCE);

            this.redissonClient = Redisson.create(this.redisConfig);
            this.redissonClient.getTopic("chocolate").addListener(String.class, this.pubSubEvent);

            proxyId = chocolatePlugin.getConfig().getString("proxyName", "proxy1");
            return true;
        } catch (Exception ex) {
            chocolatePlugin.logToConsole("&4Couldn't initialize redis connection.",ex);
            chocolatePlugin.getServer().shutdown();
            return false;
        }
    }


    private void loadDefaultConfig() {
        InputStream inputStream = ChocolatePlugin.getInstance().getClass().getClassLoader().getResourceAsStream("redis.yml");
        if (inputStream == null) return;
        try {
            OutputStream outputStream = new FileOutputStream(this.file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void shutdown() {
        this.redissonClient.shutdown();
    }

}
