package ru.hubsmc.hubscore.util;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import ru.hubsmc.hubscore.HubsCore;
import ru.hubsmc.hubscore.exception.IncorrectConfigurationException;
import ru.hubsmc.hubscore.module.loop.HubsLoop;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    private static final Pattern ipPattern = Pattern.compile("((?<![0-9])(?:(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[ ]?[.,-:; ][ ]?(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[ ]?[., ][ ]?(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[ ]?[., ][ ]?(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2}))(?![0-9]))");
    private static final Pattern webPattern = Pattern.compile("[-a-zA-Z0-9@:%_\\+.~#?&//=]{2,256}\\.[a-z]{2,4}\\b(\\/[-a-zA-Z0-9@:%_\\+~#?&//=]*)?");

    public static String[] cutFirstsStrings(String[] strings, int numberToCut) {
        String[] newStrings = new String[strings.length - numberToCut];
        System.arraycopy(strings, numberToCut, newStrings, 0, newStrings.length);
        return newStrings;
    }

    public static Map<String, String> configSectionToStringMap(ConfigurationSection section) {
        Map<String, String> answer = new HashMap<>();
        for (String key : section.getKeys(false)) {
            try {
                String s = section.getString(key);
                if (s == null) {
                    throw new IncorrectConfigurationException("String with key '" + key + "' is not a string!");
                }
                answer.put(key, section.getString(key));
            } catch (IncorrectConfigurationException e) {
                e.printStackTrace();
            }
        }
        return answer;
    }

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

    public static List<String> replaceColor(String[] strings) {
        List<String> result = new LinkedList<>();
        for (String s : strings) {
            result.add(replaceColor(s));
        }
        return result;
    }

    public static List<String> replaceColorAndSetWhite(List<String> strings) {
        List<String> result = new LinkedList<>();
        for (String s : strings) {
            result.add(ChatColor.WHITE + replaceColor(s));
        }
        return result;
    }

    public static String deleteColors(String s) {
        return s.replaceAll("&[0-9a-zA-Z]", "");
    }

    public static String setPlaceholders(String s, String... data) {
        if (data.length <= 1) {
            return s;
        } else {
            return setPlaceholders(s, Arrays.copyOf(data, data.length - 2)).replaceAll("%" + data[data.length-2] + "%", data[data.length-1]);
        }
    }

    public static List<String> setPlaceholders(List<String> strings, String... data) {
        strings.forEach(s -> setPlaceholders(s, data));
        return strings;
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

    static ArrayList<String[]> translateTextParts(String s) {
        ArrayList<String> texts = new ArrayList<>();

        for (String text : s.split("<.*?>")) {
            if (!text.equals("")) {
                texts.add(text);
            }
        }

        List<String> mods = new ArrayList<>();
        Matcher m = Pattern.compile("<.*?>").matcher(s);
        while (m.find()) {
            mods.add(m.group().replace("<", "").replace(">", ""));
        }

        ArrayList<String[]> hovAndExec = new ArrayList<>();
        for (String mod : mods) {
            hovAndExec.add(mod.split(","));
        }

        ArrayList<String[]> res = new ArrayList<>();
        for (int i = 0; i < texts.size(); i++) {
            if (hovAndExec.get(i).length > 1) {
                res.add(new String[]{texts.get(i), hovAndExec.get(i)[0], hovAndExec.get(i)[1]});
            } else {
                res.add(new String[]{texts.get(i), hovAndExec.get(i)[0], ""});
            }
        }
        return res;
    }

    public static boolean checkStringForIPPattern(String message) {
        message = message.replaceAll(" ", "");
        Matcher regexMatcher = ipPattern.matcher(message);
        while (regexMatcher.find()) {
            if (regexMatcher.group().length() != 0) {
                String text = regexMatcher.group().trim().replaceAll("http://", "").replaceAll("https://", "").split("/")[0];

                if (text.split("\\.").length > 4) {
                    String[] domains = text.split("\\.");
                    String one = domains[domains.length - 1];
                    String two = domains[domains.length - 2];
                    String three = domains[domains.length - 3];
                    String four = domains[domains.length - 4];
                    text = one + "." + two + "." + three + "." + four;
                }

                if (ipPattern.matcher(text).find()) {
                    if (!HubsLoop.goodIpsAndDomains.contains(regexMatcher.group().trim())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean checkStringForWebPattern(String message) {
        message = message.replaceAll(" ", "");
        Matcher regexMatcher = webPattern.matcher(message);
        while (regexMatcher.find()) {
            if (regexMatcher.group().length() != 0) {
                String text = regexMatcher.group().trim().replaceAll("http://", "").replaceAll("https://", "").split("/")[0];

                if (text.split("\\.").length > 2) {
                    String[] domains = text.split("\\.");
                    String toplevel = domains[domains.length - 1];
                    String second = domains[domains.length - 2];
                    text = second + "." + toplevel;
                }

                if (webPattern.matcher(text).find()) {
                    if (!HubsLoop.goodIpsAndDomains.contains(text)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String[] listOfStringsToStringsArray(List<String> strings) {
        String[] lines = new String[strings.size()];
        for (int i = 0; i < lines.length; i++) {
            lines[i] = strings.get(i);
        }
        return lines;
    }

}
