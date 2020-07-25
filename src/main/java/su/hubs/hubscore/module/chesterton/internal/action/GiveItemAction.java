package su.hubs.hubscore.module.chesterton.internal.action;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import su.hubs.hubscore.module.chesterton.internal.item.ChestertonItem;

public class GiveItemAction extends ItemAction {

    private ItemStack itemStack;

    public GiveItemAction(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public void execute(Player player, ChestertonItem item) {
        player.getInventory().addItem(itemStack);
    }

}
