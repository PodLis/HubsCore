package ru.hubsmc.hubscore.module.values.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static ru.hubsmc.hubscore.util.MessageUtils.sendPrefixMessage;

public class TopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sendPrefixMessage(sender, "Автор ещё не закончил топ. Ждите...");
        return true;
    }

}
