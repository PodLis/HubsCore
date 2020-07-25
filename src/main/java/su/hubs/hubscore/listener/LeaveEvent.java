package su.hubs.hubscore.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import su.hubs.hubscore.CoreModule;
import su.hubs.hubscore.PluginUtils;

public class LeaveEvent implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        onHubsQuit(event.getPlayer());
    }

    public static void onHubsQuit(Player player) {
        for (CoreModule coreModule : PluginUtils.getModules()) {
            coreModule.onPlayerLeave(player);
        }
        PluginUtils.getHubsServer().onPlayerQuit(player);
        PluginUtils.unloadHubsPlayer(player);
    }

}
