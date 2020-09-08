package io.alerium.chocolate.velocity.objects;

import io.alerium.chocolate.velocity.ChocolateVelocityPlugin;
import io.alerium.chocolate.velocity.events.PubSubEvent;
import lombok.RequiredArgsConstructor;
import redis.clients.jedis.JedisPubSub;

@RequiredArgsConstructor
public class PubSubListener extends JedisPubSub {

    private final ChocolateVelocityPlugin plugin;
    
    @Override
    public void onMessage(String channel, String message) {
        plugin.getServer().getEventManager().fireAndForget(new PubSubEvent(channel, message));    
    }
    
}
