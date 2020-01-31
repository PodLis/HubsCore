package ru.hubsmc.hubscore;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import ru.hubsmc.hubscore.listener.JoinEvent;
import ru.hubsmc.hubscore.listener.LeaveEvent;
import ru.hubsmc.hubscore.module.chesterton.HubsChesterton;
import ru.hubsmc.hubscore.module.essentials.HubsEssentials;
import ru.hubsmc.hubscore.module.loop.HubsLoop;
import ru.hubsmc.hubscore.module.values.HubsValues;
import ru.hubsmc.hubscore.util.UtilsCommand;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static ru.hubsmc.hubscore.PluginUtils.*;
import static ru.hubsmc.hubscore.util.ServerUtils.logConsole;

public final class HubsCore extends JavaPlugin {

    public static String CHAT_PREFIX;
    public static String SPACE_PREFIX;
    public static String CORE_PREFIX;
    public static ConfigurationSection commonMessages;

    private static HubsCore instance;

    private BukkitScheduler mainScheduler;
    private byte cycleMin;

    Map<String, CoreModule> coreModules;
    HubsServer server;
    File mainFolder, coreFolder;

    @Override
    public void onEnable() {

        // main aspects loads
        instance = this;
        getMainThings();
        coreFolder = new File(mainFolder, "HubsCore");
        mainScheduler = getServer().getScheduler();
        cycleMin = 0;

        // strings.yml loads
        FileConfiguration stringsConfig = getConfigInFolder(coreFolder, "strings");
        commonMessages = stringsConfig.getConfigurationSection("chat.common-messages");
        CHAT_PREFIX = stringsConfig.getString("chat.prefixes.hubs");
        SPACE_PREFIX = stringsConfig.getString("chat.prefixes.space");
        CORE_PREFIX = stringsConfig.getString("chat.prefixes.hubscore");

        // module enabling
        coreModules = new HashMap<>();
        coreModules.put("HubsValues", new HubsValues());
        coreModules.put("HubsChesterton", new HubsChesterton());
        coreModules.put("HubsLoop", new HubsLoop());
        coreModules.put("HubsEssentials", new HubsEssentials());
        for (CoreModule module : coreModules.values()) {
            module.onEnable();
        }

        // register basic events and basic commands
        registerEventsOfListener(new JoinEvent());
        registerEventsOfListener(new LeaveEvent());
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

}
