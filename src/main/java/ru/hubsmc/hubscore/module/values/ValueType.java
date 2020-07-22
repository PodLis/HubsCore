package ru.hubsmc.hubscore.module.values;

import org.bukkit.configuration.ConfigurationSection;
import ru.hubsmc.hubscore.PluginUtils;

public enum ValueType {

    DOLLARS("dollars"),
    HUBIXES("hubixes"),
    MANA("mana");

    private String name = "NULL";
    private String prefix = "";
    private String suffix = "";

    ValueType(String path) {
        ConfigurationSection section = PluginUtils.getConfigInCoreFolder("values").getConfigurationSection(path);
        if (section != null) {
            name = section.getString("name");
            prefix = section.getString("color");
            suffix = section.getString("symbol");
        }
    }

    public String getName() {
        return name;
    }

    public String getFormattedValue(int amount) {
        return prefix + amount + suffix;
    }

}
