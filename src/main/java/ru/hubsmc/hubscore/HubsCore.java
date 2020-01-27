package ru.hubsmc.hubscore;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import ru.hubsmc.hubscore.listener.JoinEvent;
import ru.hubsmc.hubscore.listener.LeaveEvent;
import ru.hubsmc.hubscore.module.chesterton.HubsChesterton;
import ru.hubsmc.hubscore.module.values.HubsValues;
import ru.hubsmc.hubscore.util.UtilsCommand;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static ru.hubsmc.hubscore.PluginUtils.*;
import static ru.hubsmc.hubscore.util.ServerUtils.logConsole;

public final class HubsCore extends JavaPlugin {

    public static String CHAT_PREFIX = ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "HC" + ChatColor.DARK_GREEN + "] " + ChatColor.GREEN;
    public static String CORE_PREFIX;
    private static HubsCore instance;

    private BukkitScheduler mainScheduler;
    private byte cycleMin;

    Map<String, CoreModule> coreModules;
    HubsServer server;
    File mainFolder, coreFolder;

    @Override
    public void onEnable() {
        instance = this;
        getMainThings();
        coreFolder = new File(mainFolder, "HubsCore");

        mainScheduler = getServer().getScheduler();
        cycleMin = 0;

        coreModules = new HashMap<>();
        coreModules.put("HubsValues", new HubsValues(getConfigInFolder(coreFolder, "values")));
        coreModules.put("HubsChesterton", new HubsChesterton());
        for (CoreModule module : coreModules.values()) {
            module.onEnable();
        }

        getServer().getPluginManager().registerEvents(new JoinEvent(), this);
        getServer().getPluginManager().registerEvents(new LeaveEvent(), this);

        Commands commands = new Commands();
        getCommand("hubscore").setExecutor(commands);
        getCommand("hubscore").setTabCompleter(commands);
        UtilsCommand utilsCommand = new UtilsCommand();
        getCommand("utils").setExecutor(utilsCommand);
        getCommand("utils").setTabCompleter(utilsCommand);

        server.onStart();

        mainScheduler.scheduleSyncRepeatingTask(this, () -> {
            for (CoreModule module : coreModules.values()) {
                module.onSchedule(cycleMin);
            }
            cycleMin++;
            cycleMin = cycleMin >= 60 ? 0 : cycleMin;
        }, 0L, 1200L);

    }

    @Override
    public void onDisable() {
        mainScheduler.cancelTasks(this);
        server.onStop();
        for (CoreModule module : coreModules.values()) {
            module.onDisable();
        }
    }

    void enableServer() {
        server.onResume();
    }

    void disableServer() {
        server.onPause();
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
