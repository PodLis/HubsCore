package ru.hubsmc.hubscore.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ru.hubsmc.hubscore.CoreModule;
import ru.hubsmc.hubscore.PluginUtils;

import static ru.hubsmc.hubscore.module.values.api.API.loadPlayerData;

public class JoinEvent implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        onJoinOrLogin(event.getPlayer());
    }

    public static void onJoinOrLogin(Player player) {
        loadPlayerData(player);
        for (CoreModule coreModule : PluginUtils.getModules()) {
            coreModule.onPlayerJoin(player);
        }
        PluginUtils.getHubsServer().onPlayerJoin(player);
    }

}
