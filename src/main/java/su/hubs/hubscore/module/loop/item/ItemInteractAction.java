package su.hubs.hubscore.module.loop.item;

import org.bukkit.entity.Player;

public class ItemInteractAction {

    private Player player;
    private Runnable runnable;

    public ItemInteractAction(Runnable r) {
        runnable = r;
    }

    public void run(Player player) {
        this.player = player;
        runnable.run();
    }

    public Player getPlayer() {
        return player;
    }

}
