package su.hubs.hubscore.module.chesterton.internal.action;

import org.bukkit.entity.Player;
import su.hubs.hubscore.module.chesterton.internal.item.ChestertonItem;
import su.hubs.hubscore.module.chesterton.internal.menu.ChestertonMenu;
import su.hubs.hubscore.module.chesterton.internal.parser.MenuParser;

public class OpenMenuBesideItemAction extends ItemAction {

    private ChestertonMenu thisMenu, nextMenu;
    private String nextMenuName;

    public OpenMenuBesideItemAction(ChestertonMenu thisMenu, ChestertonMenu nextMenu) {
        this.thisMenu = thisMenu;
        this.nextMenu = nextMenu;
    }

    public OpenMenuBesideItemAction(ChestertonMenu thisMenu, String nextMenuName) {
        this.thisMenu = thisMenu;
        this.nextMenuName = nextMenuName;
        this.nextMenu = null;
    }

    @Override
    public void execute(Player player, ChestertonItem item) {
        if (nextMenu == null) {
            nextMenu = MenuParser.parseChestMenu(nextMenuName);
        }
        nextMenu.setParentMenu(thisMenu.getParentMenu());
        nextMenu.open(player);
    }

}
