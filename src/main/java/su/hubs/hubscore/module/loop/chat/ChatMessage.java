package su.hubs.hubscore.module.loop.chat;

import org.bukkit.entity.Player;
import su.hubs.hubscore.module.loop.ToPlayerSendable;

import java.util.Collection;

public class ChatMessage implements ToPlayerSendable {

    String[] lines;

    public ChatMessage(String[] lines) {
        this.lines = lines;
    }

    @Override
    public void send(Collection<? extends Player> players) {
        for (Player player : players) {
            player.sendMessage(lines);
        }
    }

    @Override
    public void send(Player player) {
        player.sendMessage(lines);
    }

}
