package io.alerium.chocolate.objects;

import io.alerium.chocolate.ChocolatePlugin;
import io.alerium.chocolate.events.PubSubEvent;
import lombok.RequiredArgsConstructor;
import redis.clients.jedis.JedisPubSub;

@RequiredArgsConstructor
public class PubSubListener extends JedisPubSub {

    private final ChocolatePlugin plugin;
    
    @Override
    public void onMessage(String channel, String message) {
        plugin.getServer().getEventManager().fireAndForget(new PubSubEvent(channel, message));    
    }
    
}
