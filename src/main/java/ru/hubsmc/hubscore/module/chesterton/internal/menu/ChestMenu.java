package ru.hubsmc.hubscore.module.chesterton.internal.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import ru.hubsmc.hubscore.module.chesterton.internal.item.ChestertonItem;

import java.util.HashMap;
import java.util.Map;

public class ChestMenu extends ChestertonMenu {

    private Map<Integer, ChestertonItem> items;

    public ChestMenu(String title, int rows) {
        super(title, rows>=1 && rows<=6 ? rows*9 : 54);
        this.items = new HashMap<>();
    }

    @Override
    protected void setItemsToInventory(Inventory inventory, Player player) {
        for (Map.Entry<Integer, ChestertonItem> entry : items.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().createItemStack(player));
        }
    }

    @Override
    public ChestertonItem getItem(int slot) {
        return items.get(slot);
    }

    public void setItem(int slot, ChestertonItem item) {
        if (slot >= 0 && slot < size()) {
            items.put(slot, item);
        }
    }

}
