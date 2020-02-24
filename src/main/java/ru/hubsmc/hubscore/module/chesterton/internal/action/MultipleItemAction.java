package ru.hubsmc.hubscore.module.chesterton.internal.action;

import org.bukkit.entity.Player;

import java.util.List;

public class MultipleItemAction extends ItemAction {

    private List<ItemAction> actions;

    public MultipleItemAction(List<ItemAction> actions) {
        this.actions = actions;
    }

    @Override
    public void execute(Player player) {
        if (actions != null) {
            for (ItemAction action : actions) {
                action.execute(player);
            }
        }
    }

}
