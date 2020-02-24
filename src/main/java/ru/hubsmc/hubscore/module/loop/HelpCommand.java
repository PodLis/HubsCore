package ru.hubsmc.hubscore.module.loop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import ru.hubsmc.hubscore.HubsCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HelpCommand extends HubsCommand {

    public HelpCommand() {
        super("help", null, true, 0);
    }

    @Override
    public boolean onHubsCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if (args.length == 0) {
            HubsLoop.sendHelpMessage("menu", player);
        } else {
            StringBuilder key = new StringBuilder();
            for (String keyPart : args) {
                key.append(" ").append(keyPart);
            }
            HubsLoop.sendHelpMessage(key.toString().replaceFirst("\\s", ""), player);
        }
        return true;
    }

    @Override
    public List<String> onHubsComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completionList = new ArrayList<>();
        String partOfCommand;

        List<String> cmds = new ArrayList<>(HubsLoop.getHelpMessageNames());

        if (args.length == 1) {
            partOfCommand = args[0];
            StringUtil.copyPartialMatches(partOfCommand, cmds, completionList);
            Collections.sort(completionList);
            return completionList;
        }

        return null;
    }

}
