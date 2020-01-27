package ru.hubsmc.hubscore;

import org.bukkit.plugin.java.JavaPlugin;

public class HubsPlugin extends JavaPlugin {

    @Override
    public final void onEnable() {
        HubsCore.getInstance().enableServer();
    }

    @Override
    public final void onDisable() {
        HubsCore.getInstance().disableServer();
    }

}
