package su.hubs.hubscore;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import su.hubs.hubscore.exception.CommandNotFoundException;
import su.hubs.hubscore.exception.ConfigurationPartMissingException;
import su.hubs.hubscore.exception.HubsServerPluginMissingException;
import su.hubs.hubscore.exception.IncorrectConfigurationException;
import su.hubs.hubscore.module.loop.item.InteractItemMeta;
import su.hubs.hubscore.module.loop.board.App;
import su.hubs.hubscore.module.loop.item.ItemInteractAction;
import su.hubs.hubscore.util.StringUtils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
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

    public static FileConfiguration getConfigInServerFolder(String fileName, HubsServer server) {
        return getConfigInFolder(new File(getMainFolder(), server.getStringData("folder")), fileName);
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
    // great commandRegister system without plugin.yml (thanks ELCHILEN0)

    public static void registerCommand(JavaPlugin plugin, String name, String... aliases) {
        registerCommand(plugin, name, null, aliases);
    }

    public static void registerCommand(JavaPlugin plugin, String name, HubsPermission perm, String... aliases) {
        PluginCommand command = getCommand(name, plugin);
        if (perm != null)
            command.setPermission(perm.getPerm());
        if (aliases != null && aliases.length > 0)
            command.setAliases(Arrays.asList(aliases));
        getCommandMap().register(plugin.getDescription().getName(), command);
    }

    private static PluginCommand getCommand(String name, Plugin plugin) {
        PluginCommand command = null;
        try {
            Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            c.setAccessible(true);
            command = c.newInstance(name, plugin);
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return command;
    }

    private static CommandMap getCommandMap() {
        CommandMap commandMap = null;
        try {
            if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
                Field f = SimplePluginManager.class.getDeclaredField("commandMap");
                f.setAccessible(true);
                commandMap = (CommandMap) f.get(Bukkit.getPluginManager());
            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return commandMap;
    }

    //
    // great permissionRegister system without plugin.yml (thanks somebody once told me)

    static void registerAllPermissions(HubsPermission[] perms) {
        PluginManager pm = HubsCore.getInstance().getServer().getPluginManager();
        Set<Permission> permissions = pm.getPermissions();
        for (HubsPermission permission : perms) {
            Permission playerPermission = new Permission(permission.getPerm());
            if (!permissions.contains(playerPermission))
                pm.addPermission(playerPermission);
        }
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

    public static void setCommandExecutorAndTabCompleter(HubsCommand command) {
        setCommandExecutorAndTabCompleter(command.getName(), command);
    }

    public static void setCommandExecutorAndTabCompleter(HubsCommand... commands) {
        for (HubsCommand command : commands)
            setCommandExecutorAndTabCompleter(command.getName(), command);
    }

    public static void registerEventsOfListener(Listener listener) {
        HubsCore.getInstance().getServer().getPluginManager().registerEvents(listener, HubsCore.getInstance());
    }

    public static void scheduleSyncDelayedTask(Runnable runnable) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(HubsCore.getInstance(), runnable);
    }

    public static BukkitTask runTaskLater(Runnable runnable, long l) {
        return Bukkit.getScheduler().runTaskLater(HubsCore.getInstance(), runnable, l);
    }

    public static BukkitTask runTaskTimer(Runnable runnable, long delay, long period) {
        return Bukkit.getScheduler().runTaskTimer(HubsCore.getInstance(), runnable, delay, period);
    }

    public static void cancelTask(BukkitTask task) {
        Bukkit.getScheduler().cancelTask(task.getTaskId());
    }

    public static boolean isQueued(BukkitTask task) {
        return Bukkit.getScheduler().isQueued(task.getTaskId());
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

    public static String getBungeeServerName() {
        return HubsCore.getInstance().serverPluginsServerNamesMap.get(getServerName());
    }

}
