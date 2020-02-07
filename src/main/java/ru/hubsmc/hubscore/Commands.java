package ru.hubsmc.hubscore;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import ru.hubsmc.hubscore.exception.ServerErrorException;
import ru.hubsmc.hubscore.util.ServerUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import static ru.hubsmc.hubscore.PluginUtils.getVersion;
import static ru.hubsmc.hubscore.util.MessageUtils.*;
import static ru.hubsmc.hubscore.util.ServerUtils.logConsole;

public class Commands implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            if (command.getName().equalsIgnoreCase("hubscore")) {
                if (args.length == 0) {
                    sendPrefixMessage(sender, "&b&l Информация о плагине HubsCore:");
                    sendMessage(sender, "&5Версия:&a " + getVersion());
                    sendMessage(sender, "&5Автор и создатель плагина:&a Rosenboum");
                    return true;
                }
                switch (args[0].toLowerCase()) {

                    case "reload-all":
                    case "r":
                        if (!Permissions.RELOAD.senderHasPerm(sender)) {
                            sendNoPermMessage(sender, args[0]);
                            return true;
                        }
                        PluginUtils.reloadConfig();
                        sendPrefixMessage(sender, "Плагин успешно перезагружен");
                        return true;

                    case "reload-strings":
                    case "rs":
                        if (!Permissions.RELOAD.senderHasPerm(sender)) {
                            sendNoPermMessage(sender, args[0]);
                            return true;
                        }
                        PluginUtils.reloadStrings();
                        sendPrefixMessage(sender, "strings успешно перезагружены");
                        return true;

                    case "info":
                        if (!Permissions.RELOAD.senderHasPerm(sender)) {
                            sendNoPermMessage(sender, args[0]);
                            return true;
                        }
                        sendPrefixMessage(sender, "Секретная информация:");
                        sendMessage(sender, "");
                        sendMessage(sender, "Кто прочитал, тот полосатая сосалка");
                        return true;

                    case "server":
                        if (!Permissions.RELOAD.senderHasPerm(sender)) {
                            sendNoPermMessage(sender, args[0]);
                            return true;
                        }
                        if (args.length < 2) {
                            sendWrongUsageMessage(sender, "/hc server <server_name> [player]");
                            return true;
                        }

                        String server = args[1];
                        if (!PluginUtils.checkIfServerInServerMap(server)) {
                            sendPrefixMessage(sender, "Этого сервера нет в списке серверов!");
                            return true;
                        }

                        Player player;
                        if (args.length == 2) {
                            if (!(sender instanceof Player)) {
                                sendMustBePlayerMessage(sender, "server");
                                return true;
                            }
                            player = (Player) sender;
                        } else {
                            player = Bukkit.getPlayer(args[2]);
                            if (player == null) {
                                sendPlayerMustBeOnlineMessage(sender, args[2], "server");
                                return true;
                            }
                        }

                        try {
                            ServerUtils.changeServer(player, server);
                        } catch (Exception e) {
                            throw new ServerErrorException("Problems to change server from '" + HubsCore.getInstance().serverName + "' to '" + server + "'", e);
                        }

                        return true;

                    case "module":
                        if (args.length < 2) {
                            sendWrongUsageMessage(sender, "/hc module <module_name>");
                            return true;
                        }
                        CoreModule coreModule = HubsCore.getInstance().getModuleByName(args[1]);
                        if (coreModule != null) {
                            return coreModule.onCommandExecute(sender, command, label, args);
                        }


                    default:
                        sendUnknownCommandMessage(sender, args[0]);
                        return true;

                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            logConsole(Level.WARNING, "Some troubles with commands.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!Permissions.RELOAD.senderHasPerm(sender)) {
            return null;
        }

        List<String> completionList = new ArrayList<>();
        String partOfCommand;
        List<String> cmds;

        switch (args.length) {
            case 1:
                cmds = new ArrayList<>(Arrays.asList("reload-all", "reload-strings", "info", "module", "server"));
                partOfCommand = args[0];

                StringUtil.copyPartialMatches(partOfCommand, cmds, completionList);
                Collections.sort(completionList);
                return completionList;

            case 2:
                switch (args[0].toLowerCase()) {
                    case "module":
                        cmds = new ArrayList<>(HubsCore.getInstance().getModulesNames());
                        partOfCommand = args[1];

                        StringUtil.copyPartialMatches(partOfCommand, cmds, completionList);
                        Collections.sort(completionList);
                        return completionList;
                    case "server":
                        cmds = new ArrayList<>(HubsCore.getInstance().serverPluginsServerNamesMap.values());
                        partOfCommand = args[1];

                        StringUtil.copyPartialMatches(partOfCommand, cmds, completionList);
                        Collections.sort(completionList);
                        return completionList;
                    default:
                        return null;
                }
            case 3:
                switch (args[0].toLowerCase()) {
                    case "module":
                        CoreModule coreModule = HubsCore.getInstance().getModuleByName(args[1]);
                        if (coreModule == null) {
                            return null;
                        }
                        cmds = new ArrayList<>(coreModule.onTabComplete(sender, command, alias, args));
                        partOfCommand = args[2];

                        StringUtil.copyPartialMatches(partOfCommand, cmds, completionList);
                        Collections.sort(completionList);
                        return completionList;
                    case "server":
                        cmds = new ArrayList<>();
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (PluginUtils.isPlayerOnHubs(player))
                                cmds.add(player.getDisplayName());
                        }
                        partOfCommand = args[2];

                        StringUtil.copyPartialMatches(partOfCommand, cmds, completionList);
                        Collections.sort(completionList);
                        return completionList;
                    default:
                        return null;
                }
            default:
                return null;
        }

    }

}
