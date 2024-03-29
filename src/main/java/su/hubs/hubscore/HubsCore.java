package su.hubs.hubscore;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import su.hubs.hubscore.listener.InventoryEvent;
import su.hubs.hubscore.listener.ItemInteractEvent;
import su.hubs.hubscore.listener.JoinEvent;
import su.hubs.hubscore.listener.LeaveEvent;
import su.hubs.hubscore.module.chesterton.HubsChesterton;
import su.hubs.hubscore.module.loop.HubsLoop;
import su.hubs.hubscore.module.loop.item.InteractItemMeta;
import su.hubs.hubscore.module.loop.item.ItemInteractAction;
import su.hubs.hubscore.module.security.HubsSecurity;
import su.hubs.hubscore.module.values.HubsValues;
import su.hubs.hubscore.util.UtilsCommand;
import su.hubs.hubscore.module.donate.HubsDonate;
import su.hubs.hubscore.module.essentials.HubsEssentials;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class HubsCore extends JavaPlugin {

    public static String CHAT_PREFIX; // chat.prefixes.hubs in strings.yml
    public static String SPACE_PREFIX; // chat.prefixes.space in strings.yml
    public static String CORE_PREFIX; // chat.prefixes.hubscore in strings.yml
    public static ConfigurationSection commonMessages; // chat.common-messages in strings.yml
    public static boolean LOBBY_LIKE; // is-lobby-like in config.yml of coreFolder
    private static FileConfiguration cooldowns; // command cooldowns in cooldowns.yml

    private static HubsCore instance;

    private BukkitScheduler mainScheduler;
    private byte cycleMin;

    Map<String, CoreModule> coreModules;

    File mainFolder; // z_config folder (path to folder in config.yml of coreFolder)
    File coreFolder; // folder HubsCore in z_config

    HubsPlugin server;
    String serverName;
    Map<String, String> serverPluginsServerNamesMap; // convert map from hubs plugins names to bungee server names (HubsLobby -> lobby)

    private Map<Player, HubsPlayer> hubsPlayerMap;
    Map<InteractItemMeta, ItemInteractAction> interactItemMap;

    @Override
    public void onEnable() {

        // main aspects loads
        instance = this;
        PluginUtils.getMainThings();
        coreFolder = new File(mainFolder, "HubsCore");
        mainScheduler = getServer().getScheduler();
        cycleMin = 0;
        hubsPlayerMap = new HashMap<>();
        interactItemMap = new HashMap<>();
        serverPluginsServerNamesMap = PluginUtils.getServerMap();

        // register a way to change server and permissions
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        PluginUtils.registerAllPermissions(GlobalPermission.values());
        PluginUtils.registerAllPermissions(server.getServerPermissions());

        // strings.yml and cooldowns.yml loads
        PluginUtils.reloadStrings();
        cooldowns = PluginUtils.getConfigInFolder(mainFolder, "cooldowns");

        // modules enabling
        coreModules = new HashMap<>();
        coreModules.put("HubsValues", new HubsValues());
        coreModules.put("HubsChesterton", new HubsChesterton());
        coreModules.put("HubsLoop", new HubsLoop());
        coreModules.put("HubsEssentials", new HubsEssentials());
        coreModules.put("HubsSecurity", new HubsSecurity());
        coreModules.put("HubsDonate", new HubsDonate());
        for (CoreModule module : coreModules.values()) {
            if (!module.onEnable())
                Bukkit.shutdown();
        }

        // register basic events and basic commands
        if (!LOBBY_LIKE) {
            PluginUtils.registerEventsOfListener(new JoinEvent());
            PluginUtils.registerEventsOfListener(new LeaveEvent());
        }
        PluginUtils.registerEventsOfListener(new ItemInteractEvent());
        PluginUtils.registerEventsOfListener(new InventoryEvent());
        PluginUtils.setCommandExecutorAndTabCompleter("hubscore", new Commands());
        PluginUtils.setCommandExecutorAndTabCompleter("utils", new UtilsCommand());

        // enable HubsServer plugin
        if (!server.afterCoreStart())
            Bukkit.shutdown();

        // load a scheduler (run onSchedule() methods of all modules and a server)
        mainScheduler.scheduleSyncRepeatingTask(this, () -> {
            for (CoreModule module : coreModules.values()) {
                module.onSchedule(cycleMin);
            }
            server.onSchedule();
            cycleMin++;
            cycleMin = cycleMin >= 60 ? 0 : cycleMin;
        }, 0L, 1200L);

    }

    @Override
    public void onDisable() {
        mainScheduler.cancelTasks(this);

        server.beforeCoreStop(); // disable HubsServer plugin

        for (CoreModule module : coreModules.values()) {
            module.onDisable();
        }
    }

    void enableServer() {
        server.onPluginEnable();
    }

    void disableServer() {
        server.onPluginDisable();
    }

    CoreModule getModuleByName(String name) {
        return coreModules.getOrDefault(name, null);
    }

    Set<String> getModulesNames() {
        return coreModules.keySet();
    }

    static HubsCore getInstance() {
        return instance;
    }

    FileConfiguration getLocalConfig() {
        FileConfiguration config = null;
        File folder = getDataFolder();
        if (!folder.exists() && folder.mkdir()) {
            PluginUtils.logConsole("Local folder recreated");
        }
        File configFile = new File(folder, "config.yml");

        try {
            if (configFile.exists()) {
                config = YamlConfiguration.loadConfiguration(configFile);
                config.load(configFile);
                reloadConfig();
            } else {
                saveResource("config.yml", false);
                config = YamlConfiguration.loadConfiguration(configFile);
                PluginUtils.logConsole("The 'config.yml' file successfully created!");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return config;
    }

    public void setMainFolder(File mainFolder) {
        this.mainFolder = mainFolder;
    }

    public void setServer(HubsPlugin server) {
        this.server = server;
    }

    HubsPlayer getHubsPlayer(Player player) {
        return hubsPlayerMap.getOrDefault(player, null);
    }

    void setHubsPlayer(Player player, int dollars, int mana, int max, int regen) {
        hubsPlayerMap.put(player, new HubsPlayer(player, dollars, mana, max, regen));
    }

    void removeHubsPlayer(Player player) {
        hubsPlayerMap.remove(player).onRemove();
    }

    boolean isPlayerOnHubs(Player player) {
        return hubsPlayerMap.containsKey(player);
    }

    public static long getCooldownTime(String uuid, String command) {
        return cooldowns.getLong(uuid + "." + command, 0L);
    }

    public static void setCooldownTime(String uuid, String command) {
        cooldowns.set(uuid + "." + command, System.currentTimeMillis());
    }

}
