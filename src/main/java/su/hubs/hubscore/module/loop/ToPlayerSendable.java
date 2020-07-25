package su.hubs.hubscore.module.loop;

import org.bukkit.entity.Player;

import java.util.Collection;

public interface ToPlayerSendable {
    void send(Collection<? extends Player> players);
    void send(Player player);
}
