package su.hubs.hubscore.module.loop.chat.plugins;

import org.bukkit.entity.Player;
import su.hubs.hubscore.module.loop.HubsLoop;
import su.hubs.hubscore.util.ServerUtils;
import su.hubs.hubscore.util.StringUtils;

/**
 * @author TheJeterLP
 */
public class PluginManager implements PermissionsPlugin {

    private static PermissionsPlugin handler;

    public static PermissionsPlugin getInstance() {
        return HubsLoop.getChatManager();
    }

    public PluginManager() {
        if (ServerUtils.checkVault() && Vault.setupChat()) {
            handler = new Vault();
        } else {
            handler = new Nothing();
        }
    }

    @Override
    public String getName() {
        return handler.getName();
    }

    @Override
    public String getPrefix(Player p) {
        return handler.getPrefix(p);
    }

    @Override
    public String getSuffix(Player p) {
        return handler.getSuffix(p);
    }

    @Override
    public String[] getGroupNames(Player p) {
        return handler.getGroupNames(p);
    }

    @Override
    public String getNormalMessageFormat(Player p) {
        return StringUtils.replaceColor(handler.getNormalMessageFormat(p));
    }

    @Override
    public String getLocalMessageFormat(Player p) {
        return StringUtils.replaceColor(handler.getLocalMessageFormat(p));
    }

    @Override
    public String getGlobalMessageFormat(Player p) {
        return StringUtils.replaceColor(handler.getGlobalMessageFormat(p));
    }

}
