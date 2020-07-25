package su.hubs.hubscore.module.chesterton.internal.action;

import org.bukkit.entity.Player;
import su.hubs.hubscore.module.chesterton.internal.item.ChestertonItem;
import su.hubs.hubscore.module.chesterton.internal.menu.ChestertonMenu;

public class ReturnItemAction extends ItemAction {

    ChestertonMenu menu;

    public ReturnItemAction(ChestertonMenu menu) {
        this.menu = menu;
    }

    @Override
    public void execute(Player player, ChestertonItem item) {
        ChestertonMenu parentMenu = menu.getParentMenu();
        if (parentMenu == null) {
            menu.close(player);
        } else {
            parentMenu.open(player);
        }
    }

}
