package ru.hubsmc.hubscore.module.chesterton.internal.action;

import org.bukkit.entity.Player;
import ru.hubsmc.hubscore.module.chesterton.internal.menu.ChestertonMenu;

public class ReturnItemAction extends ItemAction {

    ChestertonMenu menu;

    public ReturnItemAction(ChestertonMenu menu) {
        this.menu = menu;
    }

    @Override
    public void execute(Player player) {
        ChestertonMenu parentMenu = menu.getParentMenu();
        if (parentMenu == null) {
            menu.close(player);
        } else {
            parentMenu.open(player);
        }
    }

}
