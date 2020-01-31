package ru.hubsmc.hubscore;

import org.bukkit.entity.Player;

public interface HubsServer {

    void afterCoreStart();

    void beforeCoreStop();

    void onPluginEnable();

    void onPluginDisable();

    void onPlayerJoin(Player player);

    void onPlayerQuit(Player player);

    void onSchedule();

}
