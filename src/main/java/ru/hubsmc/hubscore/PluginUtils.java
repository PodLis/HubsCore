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
import ru.hubsmc.hubscore.module.loop.board.App;

import java.io.File;
import java.util.Collection;
import java.util.logging.Level;

public class PluginUtils {

    public static HubsServer getHubsServer() {
        return HubsCore.getInstance().server;
    }

    public static String getVersion() {
        return HubsCore.getInstance().getDescription().getVersion();
    }

    static void reloadConfig() {
        reloadStrings();
        getModules().forEach(CoreModule::onReload);
    }

    static void reloadStrings() {
        FileConfiguration stringsConfig = getConfigInCoreFolder("strings");
        HubsCore.commonMessages = stringsConfig.getConfigurationSection("chat.common-messages");
        HubsCore.CHAT_PREFIX = stringsConfig.getString("chat.prefixes.hubs");
        HubsCore.SPACE_PREFIX = stringsConfig.getString("chat.prefixes.space");
        HubsCore.CORE_PREFIX = stringsConfig.getString("chat.prefixes.hubscore");
    }

    public static FileConfiguration getStringsConfig() {
        return getConfigInFolder(HubsCore.getInstance().coreFolder, "strings");
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

    static FileConfiguration getConfigInFolder(File folder, String fileName) {
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

    public static Collection<CoreModule> getModules() {
        return HubsCore.getInstance().coreModules.values();
    }

    public static void setCommandExecutorAndTabCompleter(String label, CommandExecutor command) {
        try {
            PluginCommand pluginCommand = HubsCore.getInstance().getCommand(label);
            if (pluginCommand == null) {
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

    public static BossBar createBossBar(String text, BarColor color, BarStyle style) {
        return HubsCore.getInstance().getServer().createBossBar(text, color, style);
    }

    public static Scoreboard createScoreboard() {
        return HubsCore.getInstance().getServer().getScoreboardManager().getNewScoreboard();
    }

    public static void runAppTaskTimer(App app) {
        app.runTaskTimer(HubsCore.getInstance(), 1L, 2L);
    }

    static void getMainThings() {

        // Get supporting configuration in some_server/plugins/HubsCore/config.yml to get HubsServer name and mainConfig path
        FileConfiguration configuration = HubsCore.getInstance().getLocalConfig();

        // Get HubsServer plugin with in-config name
        String serverName = getExtremelyNeedConfigString(configuration, "server");
        String version = getExtremelyNeedConfigString(configuration, "version");
        try {

            Plugin plugin = Bukkit.getServer().getPluginManager().loadPlugin(new File(HubsCore.getInstance().getDataFolder(), serverName + "-" + version + ".jar"));
            if (plugin instanceof HubsServer) {
                HubsCore.getInstance().setServer((HubsServer) plugin);
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

    public static void logConsole(String info) {
        logConsole(Level.INFO, info);
    }

    public static void logConsole(Level level, String message) {
        Bukkit.getLogger().log(level, "[HubsCore] " + message);
    }

}
