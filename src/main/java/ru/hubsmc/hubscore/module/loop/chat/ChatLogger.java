package ru.hubsmc.hubscore.module.loop.chat;

import org.bukkit.entity.Player;
import ru.hubsmc.hubscore.PluginUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author TheJeterLP
 */
public class ChatLogger {

    public static void writeToFile(Player player, String message) {
        BufferedWriter bw = null;
        File file = new File(PluginUtils.getMainFolder(), "logs");
        try {
            bw = new BufferedWriter(new FileWriter(file + File.separator + fileName(), true));
            bw.write(prefix(false) + "(" + player.getUniqueId() + ") " + player.getName() + ": " + message);
            bw.newLine();
        } catch (Exception ignored) {
        } finally {
            try {
                if (bw != null) {
                    bw.flush();
                    bw.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    public static void writeToAdFile(Player player, String message) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(PluginUtils.getMainFolder().getAbsolutePath() + File.separator + "ads.log", true));
            bw.write(prefix(true) + "(" + player.getUniqueId() + ") " + player.getName() + ": " + message);
            bw.newLine();
        } catch (Exception ignored) {
        } finally {
            try {
                if (bw != null) {
                    bw.flush();
                    bw.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    private static String fileName() {
        DateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        return date.format(cal.getTime()) + ".log";
    }

    private static String prefix(boolean day) {
        DateFormat date = day ? new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss] ") : new SimpleDateFormat("[HH:mm:ss] ");
        Calendar cal = Calendar.getInstance();
        return date.format(cal.getTime());
    }

}
