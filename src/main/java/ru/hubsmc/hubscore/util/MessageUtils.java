package ru.hubsmc.hubscore.util;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import ru.hubsmc.hubscore.HubsCore;
import ru.hubsmc.hubscore.PluginUtils;

public class MessageUtils {

    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(StringUtils.replaceColor(message));
    }

    public static void sendPrefixMessage(CommandSender sender, String message) {
        sendMessage(sender, HubsCore.CHAT_PREFIX + message);
    }

    public static void sendCoreMessage(CommandSender sender, String message) {
        sendMessage(sender, message);
    }

    public static ConfigurationSection getCommandNamespace(String command) {
        return PluginUtils.getStringsConfig().getConfigurationSection("chat.command-messages." + command);
    }

    public static void sendNoPermMessage(CommandSender sender, String subject) {
        sendPrefixMessage(sender, "У вас нет привилегий, чтобы использовать " + subject + "!");
    }

    public static void sendUnknownCommandMessage(CommandSender sender, String subject) {
        sendPrefixMessage(sender, "Неизвестная команда " + subject);
    }

    public static void sendNotEnoughArgsMessage(CommandSender sender, String subject) {
        sendPrefixMessage(sender, "Недостаточно аргументов, чтобы использовать " + subject + "!");
    }

    public static void sendMustBePlayerMessage(CommandSender sender) {
        sendPrefixMessage(sender, "Исполнитель должен быть игроком");
    }

    public static void sendPlayerMustBeOnlineMessage(CommandSender sender) {
        sendPrefixMessage(sender, "Игрок должен быть онлайн!");
    }

}
