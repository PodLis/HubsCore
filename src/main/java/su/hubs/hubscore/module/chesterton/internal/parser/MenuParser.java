package su.hubs.hubscore.module.chesterton.internal.parser;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import su.hubs.hubscore.module.chesterton.internal.menu.ChestMenu;

import java.io.File;

import static su.hubs.hubscore.PluginUtils.getFileToSaveParse;

public class MenuParser {

    public static ChestMenu parseChestMenu(String fileName) {
        Configuration configuration = YamlConfiguration.loadConfiguration(getFileToSaveParse(fileName));
        ChestMenu chestMenu;
        if (configuration.getString("title") == null) {
            chestMenu = new ChestMenu("TITLE NOT FOUND", 54);
        } else {
            chestMenu = new ChestMenu(configuration.getString("title"), 54);
        }

        for (String slot : configuration.getKeys(false)) {
            if (slot.equals("title")) {
                continue;
            }
            int iSlot = Integer.parseInt(slot);
            if (iSlot >= 0 && iSlot < 54) {
                chestMenu.setItem(iSlot, ItemParser.parseCustomItem(configuration.getConfigurationSection(slot), chestMenu));
            }
        }

        return chestMenu;
    }

    public static ChestMenu parseChestMenuInFolder(String fileName, File folder) {
        Configuration configuration = YamlConfiguration.loadConfiguration(
                new File(folder, fileName + ".yml")
        );
        ChestMenu chestMenu;
        if (configuration.getString("title") == null) {
            chestMenu = new ChestMenu("TITLE NOT FOUND", 54);
        } else {
            chestMenu = new ChestMenu(configuration.getString("title"), 54);
        }

        for (String slot : configuration.getKeys(false)) {
            if (slot.equals("title")) {
                continue;
            }
            int iSlot = Integer.parseInt(slot);
            if (iSlot >= 0 && iSlot < 54) {
                chestMenu.setItem(iSlot, ItemParser.parseCustomItem(configuration.getConfigurationSection(slot), chestMenu));
            }
        }

        return chestMenu;
    }

}
