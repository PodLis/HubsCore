package ru.hubsmc.hubscore;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import ru.hubsmc.hubscore.listener.ItemInteractEvent;
import ru.hubsmc.hubscore.listener.JoinEvent;
import ru.hubsmc.hubscore.listener.LeaveEvent;
import ru.hubsmc.hubscore.module.chesterton.HubsChesterton;
import ru.hubsmc.hubscore.module.essentials.HubsEssentials;
import ru.hubsmc.hubscore.module.loop.HubsLoop;
import ru.hubsmc.hubscore.module.loop.item.InteractItemMeta;
import ru.hubsmc.hubscore.module.loop.item.ItemInteractAction;
import ru.hubsmc.hubscore.module.security.HubsSecurity;
import ru.hubsmc.hubscore.module.values.HubsValues;
import ru.hubsmc.hubscore.util.UtilsCommand;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static ru.hubsmc.hubscore.PluginUtils.*;

public final class HubsCore extends JavaPlugin {

    public static String CHAT_PREFIX;
    public static String SPACE_PREFIX;
    public static String CORE_PREFIX;
    public static ConfigurationSection commonMessages;
    public static boolean LOBBY_LIKE;

    private static HubsCore instance;

    private BukkitScheduler mainScheduler;
    private byte cycleMin;

    Map<String, CoreModule> coreModules;
    File mainFolder, coreFolder;

    HubsPlugin server;
    String serverName;
    Map<String, String> serverPluginsServerNamesMap;

    private Map<Player, HubsPlayer> hubsPlayerMap;
    Map<InteractItemMeta, ItemInteractAction> interactItemMap;

    @Override
    public void onEnable() {

        // main aspects loads
        instance = this;
        getMainThings();
        coreFolder = new File(mainFolder, "HubsCore");
        mainScheduler = getServer().getScheduler();
        cycleMin = 0;
        hubsPlayerMap = new HashMap<>();
        interactItemMap = new HashMap<>();
        serverPluginsServerNamesMap = PluginUtils.getServerMap();

        // register a way to change server
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        // strings.yml loads
        reloadStrings();

        // module enabling
        coreModules = new HashMap<>();
        coreModules.put("HubsValues", new HubsValues());
        coreModules.put("HubsChesterton", new HubsChesterton());
        coreModules.put("HubsLoop", new HubsLoop());
        coreModules.put("HubsEssentials", new HubsEssentials());
        coreModules.put("HubsSecurity", new HubsSecurity());
        for (CoreModule module : coreModules.values()) {
            module.onEnable();
        }

        // register basic events and basic commands
        if (!LOBBY_LIKE) {
            registerEventsOfListener(new JoinEvent());
            registerEventsOfListener(new LeaveEvent());
        }
        registerEventsOfListener(new ItemInteractEvent());
        setCommandExecutorAndTabCompleter("hubscore", new Commands());
        setCommandExecutorAndTabCompleter("utils", new UtilsCommand());

        // enable HubsServer plugin
        server.afterCoreStart();

        // load a scheduler
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
        server.beforeCoreStop();
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
            logConsole("Local folder recreated");
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
                logConsole("The 'config.yml' file successfully created!");
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

}
