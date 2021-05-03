package io.alerium.chocolate.velocity.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import io.alerium.chocolate.velocity.ChocolateVelocityPlugin;
import io.alerium.chocolate.velocity.objects.PlayerData;
import lombok.RequiredArgsConstructor;

import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
public class PluginMessageListener {

    private final ChocolateVelocityPlugin plugin;
    
    private static final LegacyChannelIdentifier LEGACY_CHANNEL = new LegacyChannelIdentifier("Chocolate");
    private static final MinecraftChannelIdentifier MODERN_CHANNEL = MinecraftChannelIdentifier.create("chocolate", "main");
    
    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getIdentifier().equals(LEGACY_CHANNEL) && !event.getIdentifier().equals(MODERN_CHANNEL))
            return;
        
        event.setResult(PluginMessageEvent.ForwardResult.handled());
        
        if (!(event.getSource() instanceof ServerConnection))
            return;
        
        ServerConnection connection = (ServerConnection) event.getSource();
        ByteArrayDataInput input = event.dataAsDataStream();
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        
        String subChannel = input.readUTF();
        switch (subChannel.toUpperCase()) {
            case "PLAYERLIST": {
                String server = input.readUTF();
                Set<UUID> players = plugin.getCacheManager().getOnlinePlayersInServer(server.toLowerCase());
                StringBuilder sb = new StringBuilder();
                for (UUID uuid : players)
                    sb.append(uuid).append(", ");
                
                if (sb.length() != 0)
                    sb.setLength(sb.length()-2);
                
                output.writeUTF("PlayerList");
                output.writeUTF(server);
                output.writeUTF(sb.toString());
                break;
            }
            
            case "PLAYERCOUNT": {
                String server = input.readUTF();
                int players;
                if (server.equalsIgnoreCase("ALL"))
                    players = plugin.getRedisManager().getOnlinePlayers();
                else
                    players = plugin.getCacheManager().getOnlinePlayersInServer(server.toLowerCase()).size();
                
                output.writeUTF("PlayerCount");
                output.writeUTF(server);
                output.writeInt(players);
                break;
            }
            
            case "LASTONLINE": {
                String name = input.readUTF();
                PlayerData data = plugin.getCacheManager().getPlayerData(name);
                
                output.writeUTF("LastOnline");
                output.writeUTF(name);
                output.writeLong(data.getLastOnline());
                break;
            }
            
            case "PROXY": {
                String uuidS = input.readUTF();
                
                output.writeUTF("Proxy");
                output.writeUTF(uuidS);
                
                UUID uuid = UUID.fromString(uuidS);
                if (plugin.getServer().getPlayer(uuid).isPresent())
                    output.writeUTF(plugin.getRedisManager().getProxyName());
                else 
                    output.writeUTF(plugin.getCacheManager().getPlayerData(uuid).getProxy());
                break;
            }
            
            default:
                return;
        }

        connection.sendPluginMessage(event.getIdentifier(), output.toByteArray());
    }

}
