package ru.hubsmc.hubscore.listener;

import fr.xephi.authme.api.v3.AuthMeApi;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.hubsmc.hubscore.CoreModule;
import ru.hubsmc.hubscore.HubsCore;
import ru.hubsmc.hubscore.PluginUtils;

public class LeaveEvent implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (!HubsCore.LOBBY_LIKE || AuthMeApi.getInstance().isAuthenticated(event.getPlayer())) {
            PluginUtils.unloadHubsPlayer(event.getPlayer());
            for (CoreModule coreModule : PluginUtils.getModules()) {
                coreModule.onPlayerLeave(event.getPlayer());
            }
            PluginUtils.getHubsServer().onPlayerQuit(event.getPlayer());
        }
    }

}
