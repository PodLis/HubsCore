package su.hubs.hubscore.util;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import su.hubs.hubscore.HubsCore;
import su.hubs.hubscore.PluginUtils;
import su.hubs.hubscore.exception.ConfigurationPartMissingException;

import static su.hubs.hubscore.util.StringUtils.setPlaceholdersPrefixes;

public class MessageUtils {

    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(StringUtils.replaceColor(message));
    }

    public static void sendPrefixMessage(CommandSender sender, String message) {
        sendMessage(sender, HubsCore.CORE_PREFIX + message);
    }

    public static ConfigurationSection getCommandNamespace(String command) {
        return PluginUtils.getStringsConfig().getConfigurationSection("chat.command-messages." + command);
    }


    public static void sendNoPermMessage(CommandSender sender, String command) {
        sendCommonMessage(sender, "no-perm", "command", command);
    }

    public static void sendUnknownCommandMessage(CommandSender sender, String command) {
        sendCommonMessage(sender, "unknown-command", "command", command);
    }

    public static void sendWrongUsageMessage(CommandSender sender, String usage) {
        sendCommonMessage(sender, "wrong-usage", "usage", usage);
    }

    public static void sendMustBePlayerMessage(CommandSender sender, String command) {
        sendCommonMessage(sender, "must-be-a-player", "command", command);
    }

    public static void sendPlayerMustBeOnlineMessage(CommandSender sender, String player, String command) {
        sendCommonMessage(sender, "must-be-online", "player", player, "command", command);
    }

    public static void sendUnknownErrorMessage(CommandSender sender) {
        sendCommonMessage(sender, "unknown-error");
    }

    public static void sendAlreadyThatServerMessage(CommandSender sender) {
        sendCommonMessage(sender, "already-that-server");
    }

    public static void sendOnlyThatServerMessage(CommandSender sender, String server) {
        sendCommonMessage(sender, "only-that-server", "server", server);
    }

    public static void sendNotEnoughHubixesMessage(CommandSender sender) {
        sendCommonMessage(sender, "not-enough-hubixes");
    }

    private static void sendCommonMessage(CommandSender sender, String path, String... data) {
        sendMessage(sender, setPlaceholdersPrefixes(getNamespaceString(HubsCore.commonMessages, path, "common-messages"), data));
    }


    public static String getNamespaceString(ConfigurationSection section, String path, String parentPath) {
        try {
            if (section.contains(path)) {
                return section.getString(path);
            }
            throw new ConfigurationPartMissingException("chat-message with path '" + parentPath + "." + path + "' does not exist");
        } catch (ConfigurationPartMissingException e) {
            e.printStackTrace();
        }
        return "";
    }

}
