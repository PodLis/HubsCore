package su.hubs.hubscore.module.values.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.hubs.hubscore.HubsCommand;
import su.hubs.hubscore.Permissions;

import java.util.List;

public class TopCommand extends HubsCommand {

    public TopCommand() {
        super("top", Permissions.TOP, false, 0);
    }

    @Override
    public boolean onHubsCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        sendPlaceholderMessage(sender, "author-loh");
        return true;
    }

    @Override
    public List<String> onHubsComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }

}
