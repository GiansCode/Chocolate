package io.alerium.chocolate.event;

import io.alerium.chocolate.ChocolatePlugin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.redisson.api.listener.MessageListener;

@RequiredArgsConstructor
@Getter
public class PubSubEvent {

    private final String channel, message;

    public static class Handler implements MessageListener<String> {
        @Override
        public void onMessage(CharSequence charSequence, String s) {
            ChocolatePlugin.getInstance().getServer().getEventManager().fireAndForget(new PubSubEvent(String.valueOf(charSequence), s));
        }
    }
}
