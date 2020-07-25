package su.hubs.hubscore.module.loop.chat.plugins;

import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import su.hubs.hubscore.module.loop.HubsLoop;

/**
 * @author TheJeterLP
 */
public class Vault implements PermissionsPlugin {

    private static Chat chat = null;

    @Override
    public String getPrefix(Player p) {
        return chat.getPlayerPrefix(p.getWorld().getName(), p);
    }

    @Override
    public String getSuffix(Player p) {
        return chat.getPlayerSuffix(p.getWorld().getName(), p);
    }

    @Override
    public String[] getGroupNames(Player p) {
        return chat.getPlayerGroups(p.getWorld().getName(), p);
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
        return chat.getName();
    }

    protected static boolean setupChat() {
        try {
            RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
            if (chatProvider != null) {
                chat = chatProvider.getProvider();
                return chat.isEnabled();
            }
            return false;
        } catch (Throwable e) {
            return false;
        }
    }
}
