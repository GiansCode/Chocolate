package io.alerium.chocolate.velocity.objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;
 
@RequiredArgsConstructor @Getter
public class PlayerData {
    
    private final UUID uuid;
    
    private final long lastOnline;
    private final String ip;
    
    private final String proxy;
    private final String server;

}
