package ru.hubsmc.hubscore.module.loop.board;

import org.bukkit.entity.Player;
import ru.hubsmc.hubscore.PluginUtils;

import static ru.hubsmc.hubscore.module.values.api.API.*;

public class ScoreboardHolder {

    private App app;
    public Player player;

    private SlimBoard slim;

    private String vPlayer, vDollars, vHubixes, vMana, vMax, vRegen, vServer, vCustom1 = "", vCustom2 = "", vCustom3 = "", vCustom4 = "";

    public ScoreboardHolder(App app, Player player, int dollars, int mana, int max, int regen) {

        vPlayer = "" + player.getDisplayName();
        vDollars = "" + dollars;
        vHubixes = "" + getHubixes(player);
        vMana = "" + mana;
        vMax = "" + max;
        vRegen = "" + regen;
        vServer = "" + PluginUtils.getHubsServer().getStringData("tablo");

        this.player = player;

        this.app = app;

        slim = new SlimBoard(player, app.getRows().size());

        app.registerHolder(this);
    }

    public void update() {

        slim.setTitle(app.getTitle().getLine());

        int count = 0;
        for(Row row : app.getRows()) {
            String line = row.getLine();
            if (line.contains("%")) {
                slim.setLine(count, replaceValues(line));
            } else {
                slim.setLine(count, line);
            }
            count++;
        }
    }

    private String replaceValues(String s) {
        return s.replace("%player%", vPlayer)
                .replace("%dollars%", vDollars)
                .replace("%hubixes%", vHubixes)
                .replace("%mana%", vMana)
                .replace("%max%", vMax)
                .replace("%regen%", vRegen)
                .replace("%server%", vServer)
                .replace("%c1%", vCustom1)
                .replace("%c2%", vCustom2)
                .replace("%c3%", vCustom3)
                .replace("%c4%", vCustom4);
    }

    public void updateValues() {
        vPlayer = "" + player.getDisplayName();
        vDollars = "" + getDollars(player);
        vHubixes = "" + getHubixes(player);
        vMana = "" + getMana(player);
        vMax = "" + getMaxMana(player);
        vRegen = "" + getRegenMana(player);
        vServer = "" + PluginUtils.getHubsServer().getStringData("tablo");
    }

    public void updateCustomValues(String v1, String v2, String v3, String v4) {
        vCustom1 = v1;
        vCustom2 = v2;
        vCustom3 = v3;
        vCustom4 = v4;
    }

}
