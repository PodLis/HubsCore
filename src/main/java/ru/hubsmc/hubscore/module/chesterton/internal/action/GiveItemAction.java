package ru.hubsmc.hubscore.module.chesterton.internal.action;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveItemAction extends ItemAction {

    private ItemStack itemStack;

    public GiveItemAction(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public void execute(Player player) {
        player.getInventory().addItem(itemStack);
    }

}
