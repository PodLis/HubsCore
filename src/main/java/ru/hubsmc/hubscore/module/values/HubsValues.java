package ru.hubsmc.hubscore.module.values;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import ru.hubsmc.hubscore.CoreModule;
import ru.hubsmc.hubscore.Permissions;
import ru.hubsmc.hubscore.PluginUtils;
import ru.hubsmc.hubscore.module.values.api.ValuesPlayerData;
import ru.hubsmc.hubscore.module.values.commands.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ru.hubsmc.hubscore.module.values.api.API.*;
import static ru.hubsmc.hubscore.util.MessageUtils.*;

public class HubsValues extends CoreModule {

    public static int START_MANA;
    public static int START_REGEN;
    public static int OFFLINE_COEFFICIENT;
    public static int HUBIXES_TO_DOLLARS_RATE;
    public static boolean LOAD_VALUES_ON_JOIN;

    private static boolean online;

    private FileConfiguration configuration;

    private ValuesPlayerData specialDataStore;

    @Override
    public void onEnable() {
        loadFiles();

        PluginUtils.setCommandExecutorAndTabCompleter("mana", new ManaCommand());
        PluginUtils.setCommandExecutorAndTabCompleter("pay", new PayCommand());
        PluginUtils.setCommandExecutorAndTabCompleter("convert", new ConvertCommand());
        PluginUtils.setCommandExecutorAndTabCompleter("top", new TopCommand());
        PluginUtils.setCommandExecutorAndTabCompleter("update", new UpdateCommand());

        online = true;
    }

    @Override
    public void onDisable() {
        online = false;
        for (Player player : Bukkit.getOnlinePlayers()) {
            savePlayerData(player);
        }
        specialDataStore.closeConnections();
    }

    @Override
    public void onReload() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            savePlayerData(player);
        }

        loadFiles();

        for (Player player : Bukkit.getOnlinePlayers()) {
            loadPlayerData(player);
        }
    }

    @Override
    public void onPlayerJoin(Player player) {
        if (LOAD_VALUES_ON_JOIN) {
            loadPlayerData(player);
        }
    }

    @Override
    public void onPlayerLeave(Player player) {
        if (isPlayerOnline(player) && online) {
            savePlayerData(player);
        }
    }

    @Override
    public void onSchedule(byte min) {
        increaseAllOnlineMana();
        if (min % OFFLINE_COEFFICIENT == 0) increaseAllOfflineMana();
    }

    @Override
    public boolean onCommandExecute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3)
        {
            sendWrongUsageMessage(sender, "/hc module HubsValues <sub_command>");
        }

        switch (args[2].toLowerCase()) {

            case "check":

                if (!Permissions.VALUE_CHECK.senderHasPerm(sender)) {
                    sendNoPermMessage(sender, "check");
                    return true;
                }

                if (args.length < 5) {
                    sendWrongUsageMessage(sender, "check <player> [economy]");
                    return true;
                }

                if (args.length == 5) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[4]);
                    if (checkDataExist(offlinePlayer.getUniqueId().toString())) {
                        sendPrefixMessage(sender, "Player's values:");
                        for (String type : configuration.getStringList("economy-types")) {
                            sendMessage(sender, configuration.getString(type + ".name") + ": " + configuration.getString(type + ".color") + getValueFromName(offlinePlayer, type));
                        }
                        return true;
                    }
                    sendPrefixMessage(sender, "That player is not define.");
                    return true;
                }

                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[4]);
                String valueType = args[5].toLowerCase();
                if (checkDataExist(offlinePlayer.getUniqueId().toString()) && configuration.getStringList("economy-types").contains(valueType)) {
                    sendMessage(sender, configuration.getString(valueType + ".name") + ": " + configuration.getString(valueType + ".color") + getValueFromName(offlinePlayer, valueType));
                    return true;
                }
                sendPrefixMessage(sender, "That player or that economy is not define.");
                return true;

            case "set":
            case "add":
            case "remove":

                if (!Permissions.VALUE_CHANGE.senderHasPerm(sender)) {
                    sendNoPermMessage(sender, args[3].toLowerCase());
                    return true;
                }

                if (args.length < 7) {
                    sendWrongUsageMessage(sender, args[3].toLowerCase() + "<player> <economy> <amount>");
                    return true;
                }

                OfflinePlayer offlinePlayer1 = Bukkit.getOfflinePlayer(args[4]);
                String valueType1 = args[5].toLowerCase();
                String amount = args[6];
                boolean wasSavedInMemory = false;

                if (checkDataExist(offlinePlayer1.getUniqueId().toString()) && configuration.getStringList("economy-types").contains(valueType1)) {
                    try {

                        switch (args[0]) {
                            case "set":
                            {
                                wasSavedInMemory = setValueFromName(offlinePlayer1, valueType1, Math.max(Integer.parseInt(amount), 0));
                                break;
                            }
                            case "add":
                            {
                                int beforeAmount = getValueFromName(offlinePlayer1, valueType1);
                                wasSavedInMemory = setValueFromName(offlinePlayer1, valueType1, beforeAmount + Math.max(Integer.parseInt(amount), 0));
                                break;
                            }
                            case "remove":
                            {
                                int beforeAmount = getValueFromName(offlinePlayer1, valueType1);
                                wasSavedInMemory = setValueFromName(offlinePlayer1, valueType1, Math.max(beforeAmount - Math.max(Integer.parseInt(amount), 0), 0));
                                break;
                            }
                        }

                        if (wasSavedInMemory) {
                            sendPrefixMessage(sender, "This value was saved in memory:");
                        } else {
                            sendPrefixMessage(sender, "This value was saved in database:");
                        }
                        sendMessage(sender, configuration.getString(valueType1 + ".name") + ": " + configuration.getString(valueType1 + ".color") + getValueFromName(offlinePlayer1, valueType1));
                        return true;

                    } catch (NumberFormatException e) {
                        sendPrefixMessage(sender, "Not valid number");
                        return true;
                    }

                }

                sendPrefixMessage(sender, "That player or that economy is not define.");
                return true;

            default:
                sendUnknownCommandMessage(sender, label + ": " + Arrays.toString(args));
                return true;

        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] oldArgs) {
        String[] args = new String[oldArgs.length - 2];
        System.arraycopy(oldArgs, 2, args, 0, args.length);
        List<String> completionList = new ArrayList<>();
        String partOfCommand;
        List<String> cmds = new ArrayList<>();

        if (args.length == 1) {
            cmds = new ArrayList<>(Arrays.asList("check", "set", "add", "remove"));
            partOfCommand = args[0];

            StringUtil.copyPartialMatches(partOfCommand, cmds, completionList);
            Collections.sort(completionList);
            return completionList;
        }

        if (args.length == 2 && (args[0].equalsIgnoreCase("check") || args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove"))) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                cmds.add(player.getName());
            }
            partOfCommand = args[1];

            StringUtil.copyPartialMatches(partOfCommand, cmds, completionList);
            Collections.sort(completionList);
            return completionList;
        }

        if (args.length == 3 && (args[0].equalsIgnoreCase("check") || args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove"))) {
            cmds.addAll(configuration.getStringList("economy-types"));
            partOfCommand = args[2];

            StringUtil.copyPartialMatches(partOfCommand, cmds, completionList);
            Collections.sort(completionList);
            return completionList;
        }

        if (args.length == 4 && (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove"))) {
            for (int i = 1; i <= 6; i++) {
                cmds.add("" + ((int)Math.pow(10, i)));
                cmds.add("" + ((int)Math.pow(10, i))*5);
            }
            partOfCommand = args[1];

            StringUtil.copyPartialMatches(partOfCommand, cmds, completionList);
            Collections.sort(completionList);
            return completionList;
        }

        return null;
    }

    void loadFiles() {
        configuration = PluginUtils.getConfigInCoreFolder("values");
        specialDataStore = new ValuesPlayerData();
        specialDataStore.prepareToWork("jdbc:mariadb://localhost/" + configuration.getString("sql.database"),
                configuration.getString("sql.user"),
                configuration.getString("sql.password"));

        START_MANA = configuration.getInt("mana.start_amount");
        START_REGEN = configuration.getInt("regen.start_amount");
        OFFLINE_COEFFICIENT = configuration.getInt("regen.offline_coefficient");
        HUBIXES_TO_DOLLARS_RATE = configuration.getInt("dollars.rate");
        LOAD_VALUES_ON_JOIN = configuration.getBoolean("load-values-on-join");

        // small protection, if somebody (once) will join on the server before plugin completely loaded
        for (Player player : Bukkit.getOnlinePlayers()) {
            loadPlayerData(player);
        }
    }

}
