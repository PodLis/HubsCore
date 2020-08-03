package su.hubs.hubscore.module.values;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import su.hubs.hubscore.CoreModule;
import su.hubs.hubscore.HubsCore;
import su.hubs.hubscore.GlobalPermission;
import su.hubs.hubscore.PluginUtils;
import su.hubs.hubscore.module.values.api.ValuesPlayerData;
import su.hubs.hubscore.module.values.api.API;
import su.hubs.hubscore.module.values.commands.*;
import su.hubs.hubscore.util.MessageUtils;
import su.hubs.hubscore.module.values.commands.BonusCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HubsValues extends CoreModule {

    public static int START_MANA;
    public static int START_REGEN;
    public static int OFFLINE_COEFFICIENT;
    public static int HUBIXES_TO_DOLLARS_RATE;

    private static String url;
    private static String user;
    private static String pass;

    private static boolean online;

    private FileConfiguration configuration;

    private static ValuesPlayerData specialDataStore;

    @Override
    public boolean onEnable() {
        loadFiles();

        PluginUtils.setCommandExecutorAndTabCompleter("mana", new ManaCommand());
        PluginUtils.setCommandExecutorAndTabCompleter("pay", new PayCommand());
        PluginUtils.setCommandExecutorAndTabCompleter("convert", new ConvertCommand());
        PluginUtils.setCommandExecutorAndTabCompleter("top", new TopCommand());
        PluginUtils.setCommandExecutorAndTabCompleter("update", new UpdateCommand());
        PluginUtils.setCommandExecutorAndTabCompleter(new BonusCommand());

        online = true;
        return true;
    }

    @Override
    public void onDisable() {
        online = false;
        for (Player player : Bukkit.getOnlinePlayers()) {
            API.savePlayerData(player);
        }
        specialDataStore.closeConnections();
    }

    @Override
    public void onReload() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            API.savePlayerData(player);
        }

        loadFiles();

        for (Player player : Bukkit.getOnlinePlayers()) {
            API.reloadPlayerData(player);
        }
    }

    @Override
    public void onPlayerJoin(Player player) {
        if (!HubsCore.LOBBY_LIKE) {
            API.reloadPlayerData(player);
        }
    }

    @Override
    public void onPlayerLeave(Player player) {
        if (API.isPlayerOnline(player) && online) {
            API.savePlayerData(player);
        }
    }

    @Override
    public void onSchedule(byte min) {
        API.increaseAllOnlineMana();
        if (HubsCore.LOBBY_LIKE && min % OFFLINE_COEFFICIENT == 0) API.increaseAllOfflineMana();
    }

    @Override
    public boolean onCommandExecute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3)
        {
            MessageUtils.sendWrongUsageMessage(sender, "/hc module HubsValues <sub_command>");
            return true;
        }

        switch (args[2].toLowerCase()) {

            case "check":

                if (!GlobalPermission.VALUE_CHECK.senderHasPerm(sender)) {
                    MessageUtils.sendNoPermMessage(sender, "check");
                    return true;
                }

                if (args.length < 4) {
                    MessageUtils.sendWrongUsageMessage(sender, "check <player> [economy]");
                    return true;
                }

                if (args.length == 4) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[3]);
                    if (API.checkDataExist(offlinePlayer.getUniqueId().toString())) {
                        MessageUtils.sendPrefixMessage(sender, "Player's values:");
                        for (String type : configuration.getStringList("economy-types")) {
                            MessageUtils.sendMessage(sender, configuration.getString(type + ".name") + ": " + configuration.getString(type + ".color") + API.getValueFromName(offlinePlayer, type));
                        }
                        return true;
                    }
                    MessageUtils.sendPrefixMessage(sender, "That player is not define.");
                    return true;
                }

                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[3]);
                String valueType = args[4].toLowerCase();
                if (API.checkDataExist(offlinePlayer.getUniqueId().toString()) && configuration.getStringList("economy-types").contains(valueType)) {
                    MessageUtils.sendMessage(sender, configuration.getString(valueType + ".name") + ": " + configuration.getString(valueType + ".color") + API.getValueFromName(offlinePlayer, valueType));
                    return true;
                }
                MessageUtils.sendPrefixMessage(sender, "That player or that economy is not define.");
                return true;

            case "set":
            case "add":
            case "remove":

                if (!GlobalPermission.VALUE_CHANGE.senderHasPerm(sender)) {
                    MessageUtils.sendNoPermMessage(sender, args[3].toLowerCase());
                    return true;
                }

                if (args.length < 7) {
                    MessageUtils.sendWrongUsageMessage(sender, args[3].toLowerCase() + "<player> <economy> <amount>");
                    return true;
                }

                OfflinePlayer offlinePlayer1 = Bukkit.getOfflinePlayer(args[4]);
                String valueType1 = args[5].toLowerCase();
                String amount = args[6];
                boolean wasSavedInMemory = false;

                if (API.checkDataExist(offlinePlayer1.getUniqueId().toString()) && configuration.getStringList("economy-types").contains(valueType1)) {
                    try {

                        switch (args[0]) {
                            case "set":
                            {
                                wasSavedInMemory = API.setValueFromName(offlinePlayer1, valueType1, Math.max(Integer.parseInt(amount), 0));
                                break;
                            }
                            case "add":
                            {
                                int beforeAmount = API.getValueFromName(offlinePlayer1, valueType1);
                                wasSavedInMemory = API.setValueFromName(offlinePlayer1, valueType1, beforeAmount + Math.max(Integer.parseInt(amount), 0));
                                break;
                            }
                            case "remove":
                            {
                                int beforeAmount = API.getValueFromName(offlinePlayer1, valueType1);
                                wasSavedInMemory = API.setValueFromName(offlinePlayer1, valueType1, Math.max(beforeAmount - Math.max(Integer.parseInt(amount), 0), 0));
                                break;
                            }
                        }

                        if (wasSavedInMemory) {
                            MessageUtils.sendPrefixMessage(sender, "This value was saved in memory:");
                        } else {
                            MessageUtils.sendPrefixMessage(sender, "This value was saved in database:");
                        }
                        MessageUtils.sendMessage(sender, configuration.getString(valueType1 + ".name") + ": " + configuration.getString(valueType1 + ".color") + API.getValueFromName(offlinePlayer1, valueType1));
                        return true;

                    } catch (NumberFormatException e) {
                        MessageUtils.sendPrefixMessage(sender, "Not valid number");
                        return true;
                    }

                }

                MessageUtils.sendPrefixMessage(sender, "That player or that economy is not define.");
                return true;

            default:
                MessageUtils.sendUnknownCommandMessage(sender, label + ": " + Arrays.toString(args));
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
            partOfCommand = args[3];

            StringUtil.copyPartialMatches(partOfCommand, cmds, completionList);
            Collections.sort(completionList);
            return completionList;
        }

        return null;
    }

    void loadFiles() {
        configuration = PluginUtils.getConfigInCoreFolder("values");

        START_MANA = configuration.getInt("mana.start_amount");
        START_REGEN = configuration.getInt("regen.start_amount");
        OFFLINE_COEFFICIENT = configuration.getInt("regen.offline_coefficient");
        HUBIXES_TO_DOLLARS_RATE = configuration.getInt("dollars.rate");

        url = "jdbc:mariadb://localhost/" + configuration.getString("sql.database");
        user = configuration.getString("sql.user");
        pass = configuration.getString("sql.password");
        specialDataStore = new ValuesPlayerData();

        // small protection, if somebody (once) will join on the server before plugin completely loaded
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (PluginUtils.isPlayerOnHubs(player))
                API.reloadPlayerData(player);
        }
    }

    static String getUrl() {
        return url;
    }

    static String getUser() {
        return user;
    }

    static String getPass() {
        return pass;
    }

}
