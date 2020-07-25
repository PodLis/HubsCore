package su.hubs.hubscore.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import su.hubs.hubscore.Permissions;
import su.hubs.hubscore.PluginUtils;
import su.hubs.hubscore.module.loop.HubsLoop;
import su.hubs.hubscore.module.loop.chat.ChatLogger;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ServerUtils {

    public static void logConsole(String info) {
        logConsole(Level.INFO, info);
    }

    public static void logConsole(Level level, String message) {
        Bukkit.getLogger().log(level, "[HubsCore] " + message);
    }

    public static boolean playerIsOnline (String name) {
        return Bukkit.getPlayer(name) != null;
    }

    public static void broadcastMessage(String message) {
        Bukkit.getServer().broadcastMessage(message);
    }

    /**
     * Bungee cord connection method
     *
     * @param player The player to be sent to the server
     * @param server the server name
     * @return true if server the same with current player's server, false otherwise
     */
    public static boolean changeServer(Player player, String server) throws IOException {
        if (server.equals(PluginUtils.getBungeeServerName())) {
            return true;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        dataOutputStream.writeUTF("Connect");
        dataOutputStream.writeUTF(server);
        PluginUtils.sendPluginMessage(player, "BungeeCord", byteArrayOutputStream.toByteArray());
        return false;
    }

    public static boolean checkVault() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("Vault");
        return plugin != null && plugin.isEnabled();
    }

    public static boolean checkStringForAds(String msg, Player p) {
        if (Permissions.CHAT_BYPASS.senderHasPerm(p)) {
            return false;
        }
        if (StringUtils.checkStringForIPPattern(msg) || StringUtils.checkStringForWebPattern(msg)) {
            ChatLogger.writeToAdFile(p, msg);
            return true;
        }
        return false;
    }

    public static List<Player> getLocalRecipients(Player sender) {
        Location playerLocation = sender.getLocation();
        List<Player> recipients = new ArrayList<>();
        double squaredDistance = Math.pow(HubsLoop.LOCAL_RANGE, 2);
        for (Player recipient : sender.getWorld().getPlayers()) {
            if (playerLocation.distanceSquared(recipient.getLocation()) > squaredDistance) {
                continue;
            }
            recipients.add(recipient);
        }
        return recipients;
    }

}
