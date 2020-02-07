package ru.hubsmc.hubscore.util;

import org.bukkit.entity.Player;
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

}
