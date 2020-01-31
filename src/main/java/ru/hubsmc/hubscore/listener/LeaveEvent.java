package ru.hubsmc.hubscore.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.hubsmc.hubscore.CoreModule;
import ru.hubsmc.hubscore.PluginUtils;

public class LeaveEvent implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        for (CoreModule coreModule : PluginUtils.getModules()) {
            coreModule.onPlayerLeave(event.getPlayer());
        }
        PluginUtils.getHubsServer().onPlayerQuit(event.getPlayer());
    }

}
