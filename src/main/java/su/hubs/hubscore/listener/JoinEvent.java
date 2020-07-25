package su.hubs.hubscore.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import su.hubs.hubscore.CoreModule;
import su.hubs.hubscore.PluginUtils;
import su.hubs.hubscore.module.values.api.API;

public class JoinEvent implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        onJoinOrLogin(event.getPlayer());
    }

    public static void onJoinOrLogin(Player player) {
        API.loadPlayerData(player);
        for (CoreModule coreModule : PluginUtils.getModules()) {
            coreModule.onPlayerJoin(player);
        }
        PluginUtils.getHubsServer().onPlayerJoin(player);
    }

}
