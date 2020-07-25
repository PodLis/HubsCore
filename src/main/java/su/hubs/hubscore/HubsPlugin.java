package su.hubs.hubscore;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class HubsPlugin extends JavaPlugin implements HubsServer {

    @Override
    public final void onEnable() {
        HubsCore.getInstance().enableServer();
    }

    @Override
    public final void onDisable() {
        HubsCore.getInstance().disableServer();
    }

}
