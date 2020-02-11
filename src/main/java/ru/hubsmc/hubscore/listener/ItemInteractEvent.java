package ru.hubsmc.hubscore.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import ru.hubsmc.hubscore.PluginUtils;
import ru.hubsmc.hubscore.module.loop.item.InteractItemMeta;

public class ItemInteractEvent implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if ( (event.getAction() != Action.PHYSICAL) && (event.getItem() != null) ) {
            ItemMeta itemMeta = event.getItem().getItemMeta();
            if (itemMeta != null) {
                if (PluginUtils.runInteractIfExists( new InteractItemMeta(itemMeta, event.getAction(), event.getPlayer().isSneaking()), event.getPlayer() )) {
                    event.setCancelled(true);
                }
            }
        }
    }

}
