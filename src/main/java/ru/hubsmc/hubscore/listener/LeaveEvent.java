package ru.hubsmc.hubscore.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.hubsmc.hubscore.CoreModule;
import ru.hubsmc.hubscore.PluginUtils;

public class LeaveEvent implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        onHubsQuit(event.getPlayer());
    }

    public static void onHubsQuit(Player player) {
        PluginUtils.unloadHubsPlayer(player);
        for (CoreModule coreModule : PluginUtils.getModules()) {
            coreModule.onPlayerLeave(player);
        }
        PluginUtils.getHubsServer().onPlayerQuit(player);
    }

}
