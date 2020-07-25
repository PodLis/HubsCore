package su.hubs.hubscore.module.chesterton.internal.action;

import org.bukkit.entity.Player;
import su.hubs.hubscore.module.chesterton.internal.item.ChestertonItem;

public class CustomItemAction extends ItemAction {

    private Runnable runnable;

    public CustomItemAction(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void execute(Player player, ChestertonItem item) {
        runnable.run();
    }

}
