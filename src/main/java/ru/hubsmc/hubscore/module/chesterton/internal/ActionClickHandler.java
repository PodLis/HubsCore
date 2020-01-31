package ru.hubsmc.hubscore.module.chesterton.internal;

import org.bukkit.entity.Player;
import ru.hubsmc.hubscore.module.chesterton.internal.action.ItemAction;

import java.util.List;

public class ActionClickHandler implements ClickHandler {

    private List<ItemAction> actions;

    public ActionClickHandler(List<ItemAction> actions) {
        this.actions = actions;
    }

    @Override
    public boolean onClick(Player player) {
        if (actions != null) {
            for (ItemAction itemAction : actions) {
                itemAction.execute(player);
            }
        }
        return false;
    }
}
