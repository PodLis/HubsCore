package su.hubs.hubscore.module.chesterton.internal.action;

import org.bukkit.entity.Player;
import su.hubs.hubscore.module.chesterton.internal.item.ChestertonItem;

public abstract class ItemAction {

    public abstract void execute(Player player, ChestertonItem item);

}
