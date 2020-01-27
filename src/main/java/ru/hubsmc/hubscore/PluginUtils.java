package ru.hubsmc.hubscore;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import ru.hubsmc.hubscore.exception.CommandNotFoundException;
import ru.hubsmc.hubscore.exception.ConfigurationPartMissingException;
import ru.hubsmc.hubscore.exception.HubsServerPluginMissingException;
import ru.hubsmc.hubscore.exception.IncorrectConfigurationException;

import java.io.File;
import java.util.Collection;
import java.util.logging.Level;

public class PluginUtils {

    public static String getVersion() {
        return HubsCore.getInstance().getDescription().getVersion();
    }

    static void reloadConfig() {
        getMainThings();
        getModules().forEach(CoreModule::onReload);
    }

    public static FileConfiguration getStringsConfig() {
        return getConfigInFolder(HubsCore.getInstance().coreFolder, "strings");
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
                HubsCore.getInstance().saveResource(fileName + ".yml", false);
                configuration = YamlConfiguration.loadConfiguration(file);
                logConsole("The '" + fileName + ".yml' file successfully created!");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return configuration;
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

    static void getMainThings() {

        // Get supporting configuration in some_server/plugins/HubsCore/config.yml to get HubsServer name and mainConfig path
        FileConfiguration configuration = HubsCore.getInstance().getLocalConfig();

        // Get HubsServer plugin with in-config name
        String serverName = getExtremelyNeedConfigString(configuration, "server");
        try {
            if (Bukkit.getServer().getPluginManager().isPluginEnabled(serverName)) {

                Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(serverName);
                if (plugin instanceof HubsServer) {
                    HubsCore.getInstance().server = (HubsServer) plugin;
                } else {
                    throw new HubsServerPluginMissingException("plugin with name \"" + serverName + "\" is not HubsServer plugin");
                }

            } else {
                throw new HubsServerPluginMissingException("plugin with name \"" + serverName + "\" is not enable in this server");
            }
        } catch (HubsServerPluginMissingException e) {
            e.printStackTrace();
        }

        // Get main config directory with in-config path
        String path = getExtremelyNeedConfigString(configuration, "path");
        try {
            File mainFolder = new File(path);
            if (!mainFolder.exists() || !mainFolder.mkdir())
                throw new IncorrectConfigurationException("folder with path \"" + path + "\" not exists or not a directory");
            HubsCore.getInstance().mainFolder = mainFolder;
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
