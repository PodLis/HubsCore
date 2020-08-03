package su.hubs.hubscore.module.values.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import su.hubs.hubscore.HubsCommand;
import su.hubs.hubscore.GlobalPermission;
import su.hubs.hubscore.PluginUtils;
import su.hubs.hubscore.module.values.HubsValues;
import su.hubs.hubscore.util.MessageUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import static su.hubs.hubscore.module.values.api.API.*;

public class ConvertCommand extends HubsCommand {

    public ConvertCommand() {
        super("convert", GlobalPermission.CONVERT, true, 1);
    }

    @Override
    public boolean onHubsCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        Player player = (Player) sender;

        int amount;
        try {
            amount = Integer.parseInt(args[0]);
            if (amount <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            sendPlaceholderMessage(player, "not-a-number");
            return true;
        }

        if (!takeHubixes(player, amount)) {
            sendPlaceholderMessage(player, "not-enough");
            return true;
        }

        int rate = HubsValues.HUBIXES_TO_DOLLARS_RATE;

        if (rate <= 0) {
            MessageUtils.sendUnknownErrorMessage(player);
            PluginUtils.logConsole(Level.WARNING, "Invalid configuration (HUBIXES_TO_DOLLARS_RATE <= 0)");
            return true;
        }

        addDollars(player, amount * rate);

        sendPlaceholderMessage(player, "success-1",
                "hubixes_rem", String.valueOf(amount),
                "dollars_add", String.valueOf(amount*rate)
        );

        sendPlaceholderMessage(player, "success-2",
                "hubixes_now", String.valueOf(getHubixes(player)),
                "dollars_now", String.valueOf(getDollars(player))
        );

        return true;

    }

    @Override
    public List<String> onHubsComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completionList = new ArrayList<>();
        String partOfCommand;
        List<String> cmds = new ArrayList<>();

        if (args.length == 1) {
            for (int i = 1; i <= 2; i++) {
                cmds.add("" + ((int) Math.pow(10, i)));
                cmds.add("" + ((int) Math.pow(10, i))*2);
                cmds.add("" + ((int) Math.pow(10, i))*5);
            }
            partOfCommand = args[0];

            StringUtil.copyPartialMatches(partOfCommand, cmds, completionList);
            Collections.sort(completionList);
            return completionList;
        }

        return null;
    }

}
