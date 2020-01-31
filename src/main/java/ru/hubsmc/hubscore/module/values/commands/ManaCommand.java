package ru.hubsmc.hubscore.module.values.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.hubsmc.hubscore.HubsCommand;

import java.util.List;

import static ru.hubsmc.hubscore.module.values.api.API.*;

public class ManaCommand extends HubsCommand {

    public ManaCommand() {
        super("mana", null, true, 0);
    }

    @Override
    public boolean onHubsCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        sendPlaceholderMessage(player, "header");
        sendPlaceholderMessage(player, "mana", "%mana%", String.valueOf(getMana(player)));
        sendPlaceholderMessage(player, "max", "%max%", String.valueOf(getMaxMana(player)));
        sendPlaceholderMessage(player, "regen", "%regen%", String.valueOf(getRegenMana(player)));
        return true;
    }

    @Override
    public List<String> onHubsComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

}
