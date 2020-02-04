package ru.hubsmc.hubscore.module.values.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.hubsmc.hubscore.HubsCommand;

import java.util.List;

import static ru.hubsmc.hubscore.module.values.api.API.loadPlayerData;

public class UpdateCommand extends HubsCommand {

    public UpdateCommand() {
        super("update", null, true, 0);
    }

    @Override
    public boolean onHubsCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        loadPlayerData(player);
        return true;
    }

    @Override
    public List<String> onHubsComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

}
