package ru.hubsmc.hubscore.module.chesterton.internal;

import org.bukkit.entity.Player;

public interface ClickHandler {

    /**
     * @param player - the player that clicked on the item.
     * @return true if the menu should be closed, false otherwise.
     */
    public boolean onClick(Player player);

}
