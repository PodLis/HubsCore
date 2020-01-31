package ru.hubsmc.hubscore.util;

import org.bukkit.Bukkit;

import java.util.logging.Level;

public class ServerUtils {

    public static void logConsole(String info) {
        logConsole(Level.INFO, info);
    }

    public static void logConsole(Level level, String message) {
        Bukkit.getLogger().log(level, "[HubsCore] " + message);
    }

    public static boolean playerIsOnline (String name) {
        return Bukkit.getPlayer(name) != null;
    }

}
