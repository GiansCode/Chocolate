package io.alerium.chocolate.object;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Getter
@Setter
public class PlayerData {

    private final UUID uuid;
    private String ip, server, proxy;
    private Long lastOnline;

}
