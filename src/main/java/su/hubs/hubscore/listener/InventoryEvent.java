package su.hubs.hubscore.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import su.hubs.hubscore.module.chesterton.internal.ChestertonInventoryHolder;
import su.hubs.hubscore.module.chesterton.internal.item.ChestertonItem;
import su.hubs.hubscore.module.chesterton.internal.menu.ChestertonMenu;
import su.hubs.hubscore.module.chesterton.internal.special.PartialInventoryHolder;

import static su.hubs.hubscore.PluginUtils.scheduleSyncDelayedTask;

public class InventoryEvent implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof ChestertonInventoryHolder) {

            event.setCancelled(true); // First thing to do, if an exception is thrown at least the player doesn't take the item

            ChestertonMenu menu = ((ChestertonInventoryHolder) event.getInventory().getHolder()).getMenu();
            int slot = event.getRawSlot();

            if (slot >= 0 && slot < menu.size()) {

                ChestertonItem item = menu.getItem(slot);
                Player player = (Player) event.getWhoClicked();

                if (item != null && event.getInventory().getItem(slot) != null) {
                    // Closes the inventory and executes commands AFTER the event
                    scheduleSyncDelayedTask(() -> {
                        boolean close = item.onClick(player, item);
                        if (close) {
                            player.closeInventory();
                        }
                    });
                }
            }

        } else if (event.getInventory().getHolder() instanceof PartialInventoryHolder) {

            if (event.getRawSlot() == 53) {
                event.setCancelled(true);
                ChestertonItem item = ((PartialInventoryHolder) event.getInventory().getHolder()).getPartialHolderInNeed().getItem();
                scheduleSyncDelayedTask(() -> item.onClick((Player) event.getWhoClicked(), item));
            }

        }
    }

}
