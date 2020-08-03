package su.hubs.hubscore.module.loop.chat;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import su.hubs.hubscore.GlobalPermission;
import su.hubs.hubscore.PluginUtils;
import su.hubs.hubscore.module.loop.HubsLoop;
import su.hubs.hubscore.util.ServerUtils;
import su.hubs.hubscore.util.MessageUtils;
import su.hubs.hubscore.util.PlayerUtils;
import su.hubs.hubscore.util.StringUtils;

import static su.hubs.hubscore.util.StringUtils.replaceColor;

public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(final AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!GlobalPermission.CHAT_ALLOW.senderHasPerm(player)) {
            MessageUtils.sendMessage(player, StringUtils.setPlaceholdersPrefixes(PluginUtils.getStringsConfig().getString("chat.chat-messages.no-perm")));
            event.setCancelled(true);
            return;
        }

        String format = replaceColor(HubsLoop.LOCAL_FORMAT);
        boolean global = false;
        String chatMessage = event.getMessage();

        if (ServerUtils.checkStringForAds(chatMessage, player)) {
            MessageUtils.sendMessage(player, StringUtils.setPlaceholdersPrefixes(PluginUtils.getStringsConfig().getString("chat.chat-messages.no-ads-in-my-fridge")));
            event.setCancelled(true);
            return;
        }

        if (chatMessage.startsWith("!") && GlobalPermission.CHAT_GLOBAL.senderHasPerm(player)) {
            chatMessage = chatMessage.replaceFirst("!", "");
            format = replaceColor(HubsLoop.GLOBAL_FORMAT);
            global = true;
        }
        if (!global) {
            event.getRecipients().clear();
            event.getRecipients().addAll(ServerUtils.getLocalRecipients(player));
        }

        format = format.replace("%message%", "%2$s").replace("%player%", "%1$s");
        event.setFormat(PlayerUtils.replacePlayerPlaceholders(player, format));
        event.setMessage(PlayerUtils.translatePlayerMessageColorCodes(chatMessage, player));
        ChatLogger.writeToFile(event.getPlayer(), event.getMessage());
    }

}
