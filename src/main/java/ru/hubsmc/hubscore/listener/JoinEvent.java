package ru.hubsmc.hubscore.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ru.hubsmc.hubscore.CoreModule;
import ru.hubsmc.hubscore.PluginUtils;

public class JoinEvent implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        for (CoreModule coreModule : PluginUtils.getModules()) {
            coreModule.onPlayerJoin(event.getPlayer());
        }
        PluginUtils.getHubsServer().onPlayerJoin(event.getPlayer());
    }

}
