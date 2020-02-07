package ru.hubsmc.hubscore.module.loop.chat;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import ru.hubsmc.hubscore.Permissions;
import ru.hubsmc.hubscore.PluginUtils;
import ru.hubsmc.hubscore.module.loop.HubsLoop;
import ru.hubsmc.hubscore.util.ServerUtils;

import static ru.hubsmc.hubscore.util.MessageUtils.sendMessage;
import static ru.hubsmc.hubscore.util.PlayerUtils.replacePlayerPlaceholders;
import static ru.hubsmc.hubscore.util.PlayerUtils.translatePlayerMessageColorCodes;
import static ru.hubsmc.hubscore.util.StringUtils.replaceColor;
import static ru.hubsmc.hubscore.util.StringUtils.setPlaceholdersPrefixes;

public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(final AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!Permissions.CHAT_ALLOW.senderHasPerm(player)) {
            sendMessage(player, setPlaceholdersPrefixes(PluginUtils.getStringsConfig().getString("chat.chat-messages.no-perm")));
            event.setCancelled(true);
            return;
        }

        String format = replaceColor(HubsLoop.LOCAL_FORMAT);
        boolean global = false;
        String chatMessage = event.getMessage();

        if (ServerUtils.checkStringForAds(chatMessage, player)) {
            sendMessage(player, setPlaceholdersPrefixes(PluginUtils.getStringsConfig().getString("chat.chat-messages.no-ads-in-my-fridge")));
            event.setCancelled(true);
            return;
        }

        if (chatMessage.startsWith("!") && Permissions.CHAT_GLOBAL.senderHasPerm(player)) {
            chatMessage = chatMessage.replaceFirst("!", "");
            format = replaceColor(HubsLoop.GLOBAL_FORMAT);
            global = true;
        }
        if (!global) {
            event.getRecipients().clear();
            event.getRecipients().addAll(ServerUtils.getLocalRecipients(player));
        }

        format = format.replace("%message%", "%2$s").replace("%player%", "%1$s");
        event.setFormat(replacePlayerPlaceholders(player, format));
        event.setMessage(translatePlayerMessageColorCodes(chatMessage, player));
        ChatLogger.writeToFile(event.getPlayer(), event.getMessage());
    }

}
