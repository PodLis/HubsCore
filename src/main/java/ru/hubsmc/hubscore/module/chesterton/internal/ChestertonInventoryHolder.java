package ru.hubsmc.hubscore.module.chesterton.internal;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import ru.hubsmc.hubscore.module.chesterton.internal.menu.ChestertonMenu;

public class ChestertonInventoryHolder implements InventoryHolder {

    private ChestertonMenu menu;

    public ChestertonInventoryHolder(ChestertonMenu menu) {
        this.menu = menu;
    }

    @Override
    public Inventory getInventory() {
        return Bukkit.createInventory(null, menu.size());
    }

    public ChestertonMenu getMenu() {
        return menu;
    }

    public void setMenu(ChestertonMenu menu) {
        this.menu = menu;
    }

}
