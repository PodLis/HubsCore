package ru.hubsmc.hubscore.util;

import ru.hubsmc.hubscore.HubsCore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class StringUtils {

    public static String replaceSymbolsAndNull(String s) {
        return s != null ? s.replace("&", "\u00a7") : "";
    }

    public static String replaceColor(String s) {
        return s.replace("&", "\u00a7");
    }

    public static List<String> replaceColor(List<String> strings) {
        List<String> result = new LinkedList<>();
        for (String s : strings) {
            result.add(replaceColor(s));
        }
        return result;
    }

    public static String setPlaceholders(String s, String... data) {
        if (data.length <= 1) {
            return s;
        } else {
            return setPlaceholders(s, Arrays.copyOf(data, data.length - 2)).replaceAll("%" + data[data.length-2] + "%", data[data.length-1]);
        }
    }

    public static String setPlaceholdersPrefixes(String s, String... data) {
        return setPlaceholders(s.replaceAll("@H ", HubsCore.CHAT_PREFIX).replaceAll("@S ", HubsCore.SPACE_PREFIX), data);
    }

    static String[] splitExecuteMods(String s) {
        String[] result = s.split("| ", 2);
        if (result.length < 1) return new String[]{"A", "Error"};
        if (result.length < 2) return new String[]{"A", result[0]};
        result[1] = result[1].replace("| ", "");
        if (!result[0].equals("A") && !result[0].equals("U") && !result[0].equals("E")) return new String[]{"A", result[1]};
        return result;
    }

    static String getExecuteMod(String s) {
        switch (s) {
            case "A": return "suggest_command";
            case "U": return "open_url";
            case "E": return "run_command";
        }
        return "suggest_command";
    }

    static ArrayList<String[]> splitTextParts(String s) {
        String[] bef_texts = s.split("<.*?>");
        ArrayList<String> aft_texts = new ArrayList<>();

        for (String text : bef_texts) {
            if (!text.equals("")) {
                aft_texts.add(text);
            }
        }

        String[] texts = aft_texts.toArray(new String[0]);

        String links = s;
        for (String s1 : texts) {
            links = links.replace(s1, ":");
        }
        String[] mods = links.split(":");

        for (int i = 0; i < mods.length; i++) {
            mods[i] = mods[i].replace("<", "");
            mods[i] = mods[i].replace(">", "");
        }

        ArrayList<String[]> result = new ArrayList<>();
        for (String mod : mods) {
            result.add(mod.split(","));
        }

        ArrayList<String[]> res = new ArrayList<>();
        String exec;
        for (int i = 0; i < texts.length; i++) {
            if (result.get(i).length > 1) {
                exec = result.get(i)[1];
            } else {
                exec = "";
            }
            res.add(new String[]{texts[i], result.get(i)[0], exec});
        }
        return res;
    }

}
