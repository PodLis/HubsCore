package ru.hubsmc.hubscore.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import ru.hubsmc.hubscore.exception.ConfigurationPartMissingException;
import ru.hubsmc.hubscore.exception.IncorrectConfigurationException;
import ru.hubsmc.hubscore.exception.WorldNotFoundException;
import ru.hubsmc.hubscore.module.loop.title.HubsTitle;
import ru.hubsmc.hubscore.module.loop.title.TitleAnimation;

import java.util.Arrays;
import java.util.List;

import static ru.hubsmc.hubscore.PluginUtils.logConsole;
import static ru.hubsmc.hubscore.util.StringUtils.cutFirstsStrings;

public class ConfigUtils {

    public static String getStringInSection(ConfigurationSection section, String path) throws ConfigurationPartMissingException {
        if (!section.contains(path)) throw new ConfigurationPartMissingException("String with path '" + path + "' in '" + section.getName() + "' is missing");
        return section.getString(path);
    }

    public static Location parseLocation(ConfigurationSection section, String path) throws IncorrectConfigurationException, ConfigurationPartMissingException, WorldNotFoundException {
        String line = getStringInSection(section, path);
        String[] strings = line.split(":");
        World world = Bukkit.getWorld(strings[0]);
        if (world == null) throw new WorldNotFoundException(strings[0]);
        return parseStringsToLoc(cutFirstsStrings(strings, 1), world);
    }

    public static Location parseLocation(ConfigurationSection section, String path, World world) throws IncorrectConfigurationException, ConfigurationPartMissingException {
        return parseStringsToLoc(getStringInSection(section, path).split(":"), world);
    }

    private static Location parseStringsToLoc(String[] strings, World w) throws IncorrectConfigurationException {
        try {
            if (strings.length <= 4)
                return new Location(w,
                        Double.parseDouble(strings[0]),
                        Double.parseDouble(strings[1]),
                        Double.parseDouble(strings[2])
                );
            else
                return new Location(w,
                        Double.parseDouble(strings[0]),
                        Double.parseDouble(strings[1]),
                        Double.parseDouble(strings[2]),
                        Float.parseFloat(strings[3]),
                        Float.parseFloat(strings[4])
                );
        } catch (Exception e) {
            throw new IncorrectConfigurationException("Not enough or wrong information to generate Location of strings " + Arrays.toString(strings), e);
        }
    }


    public static TitleAnimation loadAnimatedTitle(ConfigurationSection section) {
        try {
            List<String> titleSList = section.getStringList("title");
            boolean repeat = section.getBoolean("repeat");
            int repeatFrom = section.getInt("start_from");
            if (repeatFrom > (titleSList.size() - 1)) {
                repeatFrom = 0;
                logConsole("start_from value is greater than the title frames in Unregister Title, will use 0 index.");
            }
            return new TitleAnimation(titleSList, repeat, repeatFrom);
        } catch (IncorrectConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static HubsTitle parseTitle(String configLine) throws IncorrectConfigurationException {
        String[] parts = configLine.split(" :: ");

        try {
            return new HubsTitle(
                    ChatColor.translateAlternateColorCodes('&', parts[0]),
                    ChatColor.translateAlternateColorCodes('&', parts[1]),
                    Integer.parseInt(parts[2]),
                    Integer.parseInt(parts[3]),
                    Integer.parseInt(parts[4]),
                    Integer.parseInt(parts[5]));
        } catch (Exception e) {
            throw new IncorrectConfigurationException("Wrong title configuration:" + configLine);
        }
    }

    public static String[][] getStringsAndKeys(ConfigurationSection section) {

        if (section == null) {
            return new String[][] {{""},{""}};
        }

        String[] keys = section.getKeys(false).toArray(new String[0]);
        String[][] result = new String[2][keys.length];

        for (int i = 0; i < keys.length; i++) {
            result[0][i] = keys[i];
            result[1][i] = (String) section.getValues(false).get(keys[i]);
        }

        return result;
    }

    public static String[] getStrings(ConfigurationSection section, String parameter) {

        if (section == null) {
            return new String[] {""};
        }

        String[] keys = section.getKeys(false).toArray(new String[0]);
        MemorySection[] memorySections = new MemorySection[keys.length];
        String[] texts = new String[keys.length];

        for (int i = 0; i < keys.length; i++) {
            memorySections[i] = (MemorySection) section.getValues(false).get(keys[i]);
            texts[i] = memorySections[i].getString(parameter);
        }

        return texts;
    }

    public static String[][] getArrayOfStrings(ConfigurationSection section, String parameter) {

        if (section == null) {
            return new String[][] {{""}};
        }

        String[] keys = section.getKeys(false).toArray(new String[0]);
        MemorySection[] memorySections = new MemorySection[keys.length];
        String[][] texts = new String[keys.length][];

        for (int i = 0; i < keys.length; i++) {
            memorySections[i] = (MemorySection) section.getValues(false).get(keys[i]);
            texts[i] = memorySections[i].getStringList(parameter).toArray(new String[0]);
        }

        return texts;
    }

    public static double[] getDoubles(ConfigurationSection section, String parameter) {

        if (section == null) {
            return new double[] {0};
        }

        String[] keys = section.getKeys(false).toArray(new String[0]);
        MemorySection[] memorySections = new MemorySection[keys.length];
        double[] doubles = new double[keys.length];

        for (int i = 0; i < keys.length; i++) {
            memorySections[i] = (MemorySection) section.getValues(false).get(keys[i]);
            doubles[i] = memorySections[i].getDouble(parameter);
        }

        return doubles;
    }

    public static int[] getIntegers(ConfigurationSection section, String parameter) {

        if (section == null) {
            return new int[] {0};
        }

        String[] keys = section.getKeys(false).toArray(new String[0]);
        MemorySection[] memorySections = new MemorySection[keys.length];
        int[] integers = new int[keys.length];

        for (int i = 0; i < keys.length; i++) {
            memorySections[i] = (MemorySection) section.getValues(false).get(keys[i]);
            integers[i] = memorySections[i].getInt(parameter);
        }

        return integers;
    }

    public static boolean[] getBooleans(ConfigurationSection section, String parameter) {

        if (section == null) {
            return new boolean[] {false};
        }

        String[] keys = section.getKeys(false).toArray(new String[0]);
        MemorySection[] memorySections = new MemorySection[keys.length];
        boolean[] booleans = new boolean[keys.length];

        for (int i = 0; i < keys.length; i++) {
            memorySections[i] = (MemorySection) section.getValues(false).get(keys[i]);
            booleans[i] = memorySections[i].getBoolean(parameter);
        }

        return booleans;
    }

}
