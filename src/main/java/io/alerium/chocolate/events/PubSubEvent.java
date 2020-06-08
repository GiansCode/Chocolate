package io.alerium.chocolate.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor @Getter
public class PubSubEvent {
    
    private final String channel;
    private final String message;
    
}
