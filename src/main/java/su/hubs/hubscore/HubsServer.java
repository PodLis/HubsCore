package su.hubs.hubscore;

import org.bukkit.entity.Player;

public interface HubsServer {

    boolean afterCoreStart();

    void beforeCoreStop();

    void onPluginEnable();

    void onPluginDisable();

    void onPlayerJoin(Player player);

    void onPlayerQuit(Player player);

    void onReload();

    void onStringsReload();

    void onSchedule();

    String getStringData(String key);

}
