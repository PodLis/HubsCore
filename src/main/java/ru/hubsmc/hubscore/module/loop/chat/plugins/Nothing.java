package ru.hubsmc.hubscore.module.loop.chat.plugins;

import org.bukkit.entity.Player;
import ru.hubsmc.hubscore.module.loop.HubsLoop;

/**
 * @author TheJeterLP
 */
public class Nothing implements PermissionsPlugin {

    @Override
    public String getPrefix(Player p) {
        return "";
    }

    @Override
    public String getSuffix(Player p) {
        return "";
    }

    @Override
    public String[] getGroupNames(Player p) {
        return new String[]{""};
    }

    @Override
    public String getNormalMessageFormat(Player p) {
        return HubsLoop.NORMAL_FORMAT;
    }

    @Override
    public String getLocalMessageFormat(Player p) {
        return HubsLoop.LOCAL_FORMAT;
    }

    @Override
    public String getGlobalMessageFormat(Player p) {
        return HubsLoop.GLOBAL_FORMAT;
    }

    @Override
    public String getName() {
        return "Nothing was found!";
    }

}
