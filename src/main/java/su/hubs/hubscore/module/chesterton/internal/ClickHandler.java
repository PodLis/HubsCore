package su.hubs.hubscore.module.chesterton.internal;

import org.bukkit.entity.Player;
import su.hubs.hubscore.module.chesterton.internal.item.ChestertonItem;

public interface ClickHandler {

    /**
     * @param player - the player that clicked on the item.
     * @return true if the menu should be closed, false otherwise.
     */
    public boolean onClick(Player player, ChestertonItem item);

}
