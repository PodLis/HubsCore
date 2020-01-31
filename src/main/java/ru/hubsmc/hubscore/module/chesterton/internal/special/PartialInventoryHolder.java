package ru.hubsmc.hubscore.module.chesterton.internal.special;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class PartialInventoryHolder implements InventoryHolder {

    PartialHolderInNeed partialHolderInNeed;

    public PartialInventoryHolder(PartialHolderInNeed partialHolderInNeed) {
        this.partialHolderInNeed = partialHolderInNeed;
    }

    @Override
    public Inventory getInventory() {
        return Bukkit.createInventory(null, 54);
    }

    public PartialHolderInNeed getPartialHolderInNeed() {
        return partialHolderInNeed;
    }

}
