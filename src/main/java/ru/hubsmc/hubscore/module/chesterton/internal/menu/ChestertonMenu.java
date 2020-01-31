package ru.hubsmc.hubscore.module.chesterton.internal.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import ru.hubsmc.hubscore.module.chesterton.internal.ChestertonInventoryHolder;
import ru.hubsmc.hubscore.module.chesterton.internal.item.ChestertonItem;

import static ru.hubsmc.hubscore.util.StringUtils.replaceColor;

public abstract class ChestertonMenu {

    private String title;
    private int slots;
    private ChestertonMenu parentMenu;

    public ChestertonMenu(String title, int slots) {
        this.title = replaceColor(title);
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
