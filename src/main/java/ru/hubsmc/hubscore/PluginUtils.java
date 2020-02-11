package ru.hubsmc.hubscore;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;
import ru.hubsmc.hubscore.exception.CommandNotFoundException;
import ru.hubsmc.hubscore.exception.ConfigurationPartMissingException;
import ru.hubsmc.hubscore.exception.HubsServerPluginMissingException;
import ru.hubsmc.hubscore.exception.IncorrectConfigurationException;
import ru.hubsmc.hubscore.module.loop.item.InteractItemMeta;
import ru.hubsmc.hubscore.module.loop.board.App;
import ru.hubsmc.hubscore.module.loop.item.ItemInteractAction;
import ru.hubsmc.hubscore.util.StringUtils;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;

/**
 * "static" class, which provides a lot of server-level methods to HubsCore, HubsModule's and HubsServer
 */

public class PluginUtils {

    //
    // extra important methods

    public static HubsServer getHubsServer() {
        return HubsCore.getInstance().server;
    }

    public static File getMainFolder() {
        return HubsCore.getInstance().mainFolder;
    }

    public static Collection<CoreModule> getModules() {
        return HubsCore.getInstance().coreModules.values();
    }

    //
    // reload methods

    static void reloadConfig() {
        reloadStrings();
        getModules().forEach(CoreModule::onReload);
        HubsCore.getInstance().server.onReload();
    }

    static void reloadStrings() {
        FileConfiguration stringsConfig = getConfigInCoreFolder("strings");
        HubsCore.commonMessages = stringsConfig.getConfigurationSection("chat.common-messages");
        HubsCore.CHAT_PREFIX = stringsConfig.getString("chat.prefixes.hubs");
        HubsCore.SPACE_PREFIX = stringsConfig.getString("chat.prefixes.space");
        HubsCore.CORE_PREFIX = stringsConfig.getString("chat.prefixes.hubscore");
        HubsCore.getInstance().server.onStringsReload();
    }

    //
    // configuration methods

    static String getExtremelyNeedConfigString(FileConfiguration configuration, String path) {
        String res;
        try {
            res = configuration.getString(path);
            if (res == null || res.equals("")) {
                throw new ConfigurationPartMissingException("Line \"" + path + "\" missing or not filled in file " + configuration.getName());
            }
        } catch (ConfigurationPartMissingException e) {
            res = null;
            e.printStackTrace();
        }
        return res;
    }

    public static FileConfiguration getConfigInFolder(File folder, String fileName) {
        File file = new File(folder, fileName + ".yml");
        FileConfiguration configuration = null;
        try {
            if (file.exists()) {
                configuration = YamlConfiguration.loadConfiguration(file);
                configuration.load(file);
                HubsCore.getInstance().reloadConfig();
            } else {
                throw new ConfigurationPartMissingException("File '" + fileName + ".yml' does not exist in '" + folder.getAbsolutePath() + "'");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return configuration;
    }

    public static FileConfiguration getConfigInCoreFolder(String fileName) {
        return getConfigInFolder(HubsCore.getInstance().coreFolder, fileName);
    }

    public static FileConfiguration getStringsConfig() {
        return getConfigInFolder(HubsCore.getInstance().coreFolder, "strings");
    }

    public static Map<String, String> getServerMap() {
        return StringUtils.configSectionToStringMap(getConfigInFolder(HubsCore.getInstance().mainFolder, "servers"));
    }

    public static File getFileToSaveParse(String fileName) {
        return new File(getMenuFolder(), fileName + ".yml");
    }

    private static File getMenuFolder() {
        File folder = new File(HubsCore.getInstance().coreFolder, "menu");
        if (!folder.exists() && folder.mkdir()) {
            logConsole("Menu folder recreated");
        }
        return folder;
    }

    public static boolean menuFileExists(String name) {
        return (new File(getMenuFolder(), name + ".yml")).exists();
    }

    //
    // HubsPlayer methods

    public static void loadPlayerAsHubsPlayer(Player player, int dollars, int mana, int max, int regen) {
        HubsCore.getInstance().setHubsPlayer(player, dollars, mana, max, regen);
    }

    public static void unloadHubsPlayer(Player player) {
        HubsCore.getInstance().removeHubsPlayer(player);
    }

    public static boolean isPlayerOnHubs(Player player) {
        return HubsCore.getInstance().isPlayerOnHubs(player);
    }

    public static HubsPlayer getHubsPlayer(Player player) {
        return HubsCore.getInstance().getHubsPlayer(player);
    }

    //
    // ItemInteract methods

    public static ItemInteractAction getItemInteractAction(InteractItemMeta interactItemMeta) {
        return HubsCore.getInstance().interactItemMap.get(interactItemMeta);
    }

    public static void registerItemInteract(InteractItemMeta interactItemMeta, Runnable runnable) {
        HubsCore.getInstance().interactItemMap.put(interactItemMeta, new ItemInteractAction(runnable));
    }

    public static void unregisterItemInteract(InteractItemMeta interactItemMeta) {
        HubsCore.getInstance().interactItemMap.remove(interactItemMeta);
    }

    public static boolean runInteractIfExists(InteractItemMeta interactItemMeta, Player player) {
        if (HubsCore.getInstance().interactItemMap.containsKey(interactItemMeta)) {
            getItemInteractAction(interactItemMeta).run(player);
            return true;
        }
        return false;
    }

    //
    // server-subs methods

    public static void setCommandExecutorAndTabCompleter(String label, CommandExecutor command) {
        try {
            PluginCommand pluginCommand = HubsCore.getInstance().getCommand(label);
            if (pluginCommand == null) {
                pluginCommand = HubsCore.getInstance().server.getCommand(label);
                if (pluginCommand == null)
                    throw new CommandNotFoundException("Command '" + label + "' is not registered");
            } else {
                pluginCommand.setExecutor(command);
                if (command instanceof TabCompleter) {
                    pluginCommand.setTabCompleter((TabCompleter) command);
                }
            }
        } catch (CommandNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void registerEventsOfListener(Listener listener) {
        HubsCore.getInstance().getServer().getPluginManager().registerEvents(listener, HubsCore.getInstance());
    }

    public static void scheduleSyncDelayedTask(Runnable runnable) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(HubsCore.getInstance(), runnable);
    }

    public static void runTaskLater(Runnable runnable, long l) {
        Bukkit.getScheduler().runTaskLater(HubsCore.getInstance(), runnable, l);
    }

    public static BossBar createBossBar(String text, BarColor color, BarStyle style) {
        return HubsCore.getInstance().getServer().createBossBar(text, color, style);
    }

    public static Scoreboard createScoreboard() {
        return HubsCore.getInstance().getServer().getScoreboardManager().getNewScoreboard();
    }

    public static void runAppTaskTimer(App app) {
        app.runTaskTimer(HubsCore.getInstance(), 1L, 2L);
    }

    public static void sendPluginMessage(Player player, String s, byte[] bytes) {
        player.sendPluginMessage(HubsCore.getInstance(), s, bytes);
    }

    public static boolean checkIfServerInServerMap(String server) {
        return HubsCore.getInstance().serverPluginsServerNamesMap.containsValue(server);
    }

    //
    // really main method

    static void getMainThings() {

        // Get supporting configuration in some_server/plugins/HubsCore/config.yml to get HubsServer name and mainConfig path
        FileConfiguration configuration = HubsCore.getInstance().getLocalConfig();

        // Get HubsServer plugin with in-config name
        String serverName = getExtremelyNeedConfigString(configuration, "server");
        String version = getExtremelyNeedConfigString(configuration, "version");
        try {

            Plugin plugin = Bukkit.getServer().getPluginManager().loadPlugin(new File(HubsCore.getInstance().getDataFolder(), serverName + "-" + version + ".jar"));
            if (plugin instanceof HubsServer) {
                HubsCore.getInstance().setServer((HubsPlugin) plugin);
                HubsCore.getInstance().serverName = serverName;
            } else {
                throw new HubsServerPluginMissingException("plugin with name \"" + serverName + "\" is not HubsServer plugin");
            }

        } catch (HubsServerPluginMissingException | InvalidDescriptionException | InvalidPluginException e) {
            e.printStackTrace();
        }

        // Get main config directory with in-config path
        String path = getExtremelyNeedConfigString(configuration, "path");
        try {
            File mainFolder = new File(path);
            if (!mainFolder.exists() || !mainFolder.isDirectory())
                throw new IncorrectConfigurationException("folder with path \"" + path + "\" not exists or not a directory");
            HubsCore.getInstance().setMainFolder(mainFolder);
        } catch (IncorrectConfigurationException e) {
            e.printStackTrace();
        }

        // Get an important constant
        String lobbyLike = getExtremelyNeedConfigString(configuration, "is-lobby-like");
        try {
            try {
                HubsCore.LOBBY_LIKE = Boolean.parseBoolean(lobbyLike);
            } catch (NumberFormatException e) {
                throw new IncorrectConfigurationException("'" + lobbyLike + "' in 'is-lobby-like' is not a boolean value");
            }
        } catch (IncorrectConfigurationException e) {
            e.printStackTrace();
        }

    }

    //
    // sub-info methods

    public static void logConsole(String info) {
        logConsole(Level.INFO, info);
    }

    public static void logConsole(Level level, String message) {
        Bukkit.getLogger().log(level, "[HubsCore] " + message);
    }

    public static String getVersion() {
        return HubsCore.getInstance().getDescription().getVersion();
    }

    public static String getServerName() {
        return HubsCore.getInstance().serverName;
    }

}
