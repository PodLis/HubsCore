package su.hubs.hubscore.module.loop.boss;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import su.hubs.hubscore.PluginUtils;
import su.hubs.hubscore.module.loop.ToPlayerSendable;

import java.util.Collection;

public class HubsBar implements ToPlayerSendable {

    private BossBar bossBar;

    public HubsBar(String text, BarColor color, BarStyle style, double progress) {
        bossBar = PluginUtils.createBossBar(text, color, style);
        bossBar.setProgress(progress);
    }

    @Override
    public void send(Collection<? extends Player> players) {
        for (Player player : players) {
            bossBar.addPlayer(player);
        }
    }

    @Override
    public void send(Player player) {
        bossBar.addPlayer(player);
    }

    public void clean() {
        bossBar.removeAll();
    }
}
