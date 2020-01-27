package ru.hubsmc.hubscore.util;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import ru.hubsmc.hubscore.Permissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import static ru.hubsmc.hubscore.util.MessageUtils.*;
import static ru.hubsmc.hubscore.util.ServerUtils.logConsole;

public class UtilsCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            if (command.getName().equalsIgnoreCase("utils")) {
                if (args.length == 0) {
                    sendPrefixMessage(sender, "&b&l На данный момент команда имеет следующие подкоманды:");
                    sendMessage(sender,
                            "&5parse <имя-1> [имя-2] ... [имя-n]:&a При наведении на сундук, команда запарсит его содержимое в конфиг" +
                            "\nи сохранит в файле <имя-1>-[имя-2]-...-[имя-n].yml");
                    sendMessage(sender, "&5rename <имя>:&a Команда переименует предмет, что вы держите в руках");
                    return true;
                }

                if (!Permissions.UTILS.senderHasPerm(sender)) {
                    sendNoPermMessage(sender, args[0]);
                    return true;
                }

                switch (args[0].toLowerCase()) {

                    case "parse":
                        sendPrefixMessage(sender, "Парсинг временно недоступен");
                        return true;

                    case "rename":
                        sendPrefixMessage(sender, "Переименовывание временно недоступно");
                        return true;

                    default:
                        sendUnknownCommandMessage(sender, args[0]);
                        return true;

                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            logConsole(Level.WARNING, "Some troubles with utils.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completionList = new ArrayList<>();
        String partOfCommand;
        List<String> cmds = new ArrayList<>();

        switch (args.length) {
            case 1:
                cmds = new ArrayList<>(getCmds(sender));
                partOfCommand = args[0];

                StringUtil.copyPartialMatches(partOfCommand, cmds, completionList);
                Collections.sort(completionList);
                return completionList;

            default:
                return null;
        }

    }

    private List<String> getCmds(CommandSender sender) {
        return new ArrayList<>(Arrays.asList("parse", "rename"));
    }

}
