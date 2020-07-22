package ru.hubsmc.hubscore.module.donate.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import ru.hubsmc.hubscore.HubsCommand;

import java.util.List;

public class DonateCommand extends HubsCommand {

    public DonateCommand() {
        super("donate", null, true, 0);
    }

    @Override
    public boolean onHubsCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return false;
    }

    @Override
    public List<String> onHubsComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }

}
