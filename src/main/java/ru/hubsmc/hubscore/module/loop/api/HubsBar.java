package ru.hubsmc.hubscore.module.loop.api;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import ru.hubsmc.hubscore.PluginUtils;
import ru.hubsmc.hubscore.module.loop.HubsLoop;

import java.util.Collection;

public class HubsBar implements ToPlayerSendable{

    private BossBar bossBar;

    public HubsBar(HubsLoop plugin, String text, BarColor color, BarStyle style, double progress) {
        bossBar = PluginUtils.createBossBar(text, color, style);
        bossBar.setProgress(progress);
    }

    @Override
    public void send(Collection<? extends Player> players) {
        for (Player player : players) {
            bossBar.addPlayer(player);
        }
    }

    public void clean() {
        bossBar.removeAll();
    }
}
