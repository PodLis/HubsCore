package su.hubs.hubscore.module.loop.action;

import net.minecraft.server.v1_16_R1.ChatMessageType;
import net.minecraft.server.v1_16_R1.IChatBaseComponent;
import net.minecraft.server.v1_16_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_16_R1.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import su.hubs.hubscore.module.loop.ToPlayerSendable;

import java.util.Collection;

public class ActionBar implements ToPlayerSendable {

    private IChatBaseComponent component;

    private int displayTime;

    public int getExtraDisplayTime() {
        return displayTime - 3;
    }

    public ActionBar(String message, int displayTime) {
        component = ChatSerializer.a("{\"text\":\"" + message + "\"}");
        this.displayTime = Math.max(displayTime, 3);
    }

    @Override
    public void send(Collection<? extends Player> players) {
        for (Player player : players) {
            PacketPlayOutChat packet = new PacketPlayOutChat(component, ChatMessageType.GAME_INFO, player.getUniqueId());
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
        }
    }

    @Override
    public void send(Player player) {
        PacketPlayOutChat packet = new PacketPlayOutChat(component, ChatMessageType.GAME_INFO, player.getUniqueId());
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
    }
}
