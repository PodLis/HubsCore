package ru.hubsmc.hubscore.util;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import ru.hubsmc.hubscore.Permissions;
import ru.hubsmc.hubscore.module.loop.chat.plugins.PluginManager;

import java.util.regex.Pattern;

import static ru.hubsmc.hubscore.util.StringUtils.replaceColor;

public class PlayerUtils {

    private static final Pattern chatColorPattern = Pattern.compile("(?i)&([0-9A-F])");
    private static final Pattern chatMagicPattern = Pattern.compile("(?i)&([K])");
    private static final Pattern chatBoldPattern = Pattern.compile("(?i)&([L])");
    private static final Pattern chatStrikethroughPattern = Pattern.compile("(?i)&([M])");
    private static final Pattern chatUnderlinePattern = Pattern.compile("(?i)&([N])");
    private static final Pattern chatItalicPattern = Pattern.compile("(?i)&([O])");
    private static final Pattern chatResetPattern = Pattern.compile("(?i)&([R])");

    public static String replacePlayerPlaceholders(Player player, String format) {
        String result = format;
        result = result.replace("%displayname%", player.getDisplayName());
        result = result.replace("%prefix%", PluginManager.getInstance().getPrefix(player));
        result = result.replace("%suffix%", PluginManager.getInstance().getSuffix(player));
        result = result.replace("%player%", player.getDisplayName());
        result = result.replace("%world%", player.getWorld().getName());
        result = result.replace("%group%", PluginManager.getInstance().getGroupNames(player)[0]);
        return replaceColor(result);
    }

    public static String translatePlayerMessageColorCodes(String string, Player p) {
        if (string == null) {
            return "";
        }
        String newstring = string;
        if (Permissions.CHAT_COLOR.senderHasPerm(p)) {
            newstring = chatColorPattern.matcher(newstring).replaceAll("\u00A7$1");
        }
        if (Permissions.CHAT_MAGIC.senderHasPerm(p)) {
            newstring = chatMagicPattern.matcher(newstring).replaceAll("\u00A7$1");
        }
        if (Permissions.CHAT_BOLD.senderHasPerm(p)) {
            newstring = chatBoldPattern.matcher(newstring).replaceAll("\u00A7$1");
        }
        if (Permissions.CHAT_STRIKETHROUGH.senderHasPerm(p)) {
            newstring = chatStrikethroughPattern.matcher(newstring).replaceAll("\u00A7$1");
        }
        if (Permissions.CHAT_UNDERLINE.senderHasPerm(p)) {
            newstring = chatUnderlinePattern.matcher(newstring).replaceAll("\u00A7$1");
        }
        if (Permissions.CHAT_ITALIC.senderHasPerm(p)) {
            newstring = chatItalicPattern.matcher(newstring).replaceAll("\u00A7$1");
        }
        if (Permissions.CHAT_RESET.senderHasPerm(p)) {
            newstring = chatResetPattern.matcher(newstring).replaceAll("\u00A7$1");
        }
        return newstring;
    }

    public static void teleport(Player player, Location location) {
        player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    /**
     * need to realize!!!
     * teleport player to location if location is safe
     * otherwise teleport player to nearest safe location
     * @param player an online player to teleport
     * @param location a place to teleport
     * @return true if location is safe, false otherwise
     */
    public static boolean safeTeleport(Player player, Location location) {
        return false;
    }

    /**
     * clear player's inventory
     * @param player an online player, which inventory will been clear
     */
    public static void clearInventory(Player player) {
        player.getInventory().clear();
    }

    /**
     * clear player's potion effects
     * @param player an online player, which potion effects will been clear
     */
    public static void clearEffects(Player player) {
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }
    }

    /**
     * set player's game mode to gameMode
     * @param player an online player, which game mode will been change
     * @param gameMode a game mode to set
     */
    public static void setGameMode(Player player, GameMode gameMode) {
        player.setGameMode(gameMode);
    }

}
