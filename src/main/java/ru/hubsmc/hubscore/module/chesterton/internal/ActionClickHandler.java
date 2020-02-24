package ru.hubsmc.hubscore.module.chesterton.internal;

import org.bukkit.entity.Player;
import ru.hubsmc.hubscore.module.chesterton.internal.action.ItemAction;

import java.util.List;

public class ActionClickHandler implements ClickHandler {

    private ItemAction action;

    public ActionClickHandler(ItemAction action) {
        this.action = action;
    }

    @Override
    public boolean onClick(Player player) {
        if (action != null) {
            action.execute(player);
        }
        return false;
    }
}
