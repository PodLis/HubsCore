package su.hubs.hubscore;

import org.bukkit.command.CommandSender;

public interface HubsPermission {

    boolean senderHasPerm(CommandSender sender);

    String getPerm();
}
