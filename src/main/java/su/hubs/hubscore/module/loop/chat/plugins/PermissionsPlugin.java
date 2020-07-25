package su.hubs.hubscore.module.loop.chat.plugins;

import org.bukkit.entity.Player;

/**
 *
 * @author TheJeterLP
 */
public interface PermissionsPlugin {

    public String getName();

    public String getPrefix(Player p);

    public String getSuffix(Player p);

    public String[] getGroupNames(Player p);

    public String getNormalMessageFormat(Player p);

    public String getLocalMessageFormat(Player p);

    public String getGlobalMessageFormat(Player p);
}
