package ru.hubsmc.hubscore.module.chesterton.internal.action;

import org.bukkit.entity.Player;

public class CustomItemAction extends ItemAction {

    private Runnable runnable;

    public CustomItemAction(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void execute(Player player) {
        runnable.run();
    }

}
