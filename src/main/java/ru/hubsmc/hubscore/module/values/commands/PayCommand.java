package ru.hubsmc.hubscore.module.values.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import ru.hubsmc.hubscore.HubsCommand;
import ru.hubsmc.hubscore.Permissions;
import ru.hubsmc.hubscore.module.values.event.PayEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru.hubsmc.hubscore.module.values.api.API.*;

public class PayCommand extends HubsCommand {

    public PayCommand() {
        super("pay", Permissions.PAY_NORMAL, true, 2);
    }

    @Override
    public boolean onHubsCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        Player player = (Player) sender;

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sendPlaceholderMessage(sender, "not-a-number");
            return true;
        }

        if (args[0].equals("*")) {
            if (!Permissions.PAY_ALL.senderHasPerm(player)) {
                sendPlaceholderMessage(sender, "not-to-all");
                return true;
            }
            sendPlaceholderMessage(sender, "pay-all");
            return true;
        }

        Player receiver = Bukkit.getPlayer(args[0]);

        if (receiver == null) {
            sendPlaceholderMessage(sender, "rec-not-online");
            return true;
        }

        if (player.getUniqueId().toString().equals(receiver.getUniqueId().toString())) {
            sendPlaceholderMessage(sender, "stop-self-licks");
            return true;
        }

        PayEvent event = new PayEvent(player, receiver, amount);
        Bukkit.getServer().getPluginManager().callEvent(event);
        boolean isReceiverOnline;

        if (!event.isCancelled()) {
            if (takeDollars(player, amount) == 0) {
                sendPlaceholderMessage(sender, "not-enough");
                return true;
            }
            isReceiverOnline = addDollars(receiver, amount);
        } else {
            return true;
        }

        sendPlaceholderMessage(sender, "success-to-player",
                "amount", String.valueOf(amount),
                "rec_name", receiver.getName(),
                "player_now", String.valueOf(getDollars(player))
        );

        if (isReceiverOnline && receiver.getPlayer() != null) {
            sendPlaceholderMessage(sender, "not-a-number",
                    "player_name", player.getName(),
                    "amount", String.valueOf(amount),
                    "rec_now", String.valueOf(getDollars(receiver))
            );
        }

        return true;
    }

    @Override
    public List<String> onHubsComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completionList = new ArrayList<>();
        String partOfCommand;
        List<String> cmds = new ArrayList<>();

        if (args.length == 1) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                cmds.add(player.getName());
            }
            if (Permissions.PAY_NORMAL.senderHasPerm(sender))
                cmds.add("*");
            partOfCommand = args[0];

            StringUtil.copyPartialMatches(partOfCommand, cmds, completionList);
            Collections.sort(completionList);
            return completionList;
        }

        if (args.length == 2) {
            for (int i = 1; i <= 6; i++) {
                cmds.add("" + ((int) Math.pow(10, i)));
                cmds.add("" + ((int) Math.pow(10, i))*5);
            }
            partOfCommand = args[1];

            StringUtil.copyPartialMatches(partOfCommand, cmds, completionList);
            Collections.sort(completionList);
            return completionList;
        }

        return null;
    }

}
