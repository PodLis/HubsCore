package ru.hubsmc.hubscore.listener;

import fr.xephi.authme.events.LoginEvent;
import fr.xephi.authme.events.LogoutEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SuccessLoginEvent implements Listener {

    @EventHandler
    public void onLogin(LoginEvent event) {
        JoinEvent.onJoinOrLogin(event.getPlayer());
    }

    @EventHandler
    public void onLogout(LogoutEvent event) {
        event.getPlayer().kickPlayer("LOGOUT IS NOT ALLOWED HERE!");
    }

}