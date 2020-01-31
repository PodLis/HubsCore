package ru.hubsmc.hubscore.module.values.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import ru.hubsmc.hubscore.HubsCommand;
import ru.hubsmc.hubscore.Permissions;

import java.util.List;

public class TopCommand extends HubsCommand {

    public TopCommand() {
        super("top", Permissions.TOP, false, 0);
    }

    @Override
    public boolean onHubsCommand(CommandSender sender, Command command, String label, String[] args) {
        sendPlaceholderMessage(sender, "author-loh");
        return true;
    }

    @Override
    public List<String> onHubsComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

}
