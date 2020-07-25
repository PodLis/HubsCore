package su.hubs.hubscore.module.chesterton.internal.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import su.hubs.hubscore.module.chesterton.internal.ChestertonInventoryHolder;
import su.hubs.hubscore.module.chesterton.internal.item.ChestertonItem;
import su.hubs.hubscore.util.StringUtils;

public abstract class ChestertonMenu {

    private String title;
    private int slots;
    private ChestertonMenu parentMenu;

    public ChestertonMenu(String title, int slots) {
        this.title = StringUtils.replaceColor(title);
        this.slots = slots;
    }

    public int size() {
        return slots;
    }

    public String getTitle() {
        return title;
    }

    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(new ChestertonInventoryHolder(this), slots, title);
        setItemsToInventory(inventory, player);
        player.openInventory(inventory);
    }

    public void close(Player player) {
        player.closeInventory();
    }

    public ChestertonMenu getParentMenu() {
        return parentMenu;
    }

    public void setParentMenu(ChestertonMenu parentMenu) {
        this.parentMenu = parentMenu;
    }

    abstract protected void setItemsToInventory(Inventory inventory, Player player);

    abstract public ChestertonItem getItem(int slot);

}
