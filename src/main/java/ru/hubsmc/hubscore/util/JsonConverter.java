package ru.hubsmc.hubscore.util;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonConverter {

    private static Map<String, String> loopHovers = new HashMap<>();
    private static Map<String, String[]> loopExecutes = new HashMap<>();
    private static Map<String, String> helpHovers = new HashMap<>();
    private static Map<String, String[]> helpExecutes = new HashMap<>();
    private static Map<String, String> rawtextHovers = new HashMap<>();
    private static Map<String, String[]> rawtextExecutes = new HashMap<>();

    public static void setLoopHoversExecutes(String[] hoverNames, String[] hoverValues, String[] executeNames, String[] executeValues) {
        if (hoverNames.length == hoverValues.length && executeNames.length == executeValues.length) {
            for (int i = 0; i < hoverNames.length; i++) {
                JsonConverter.loopHovers.put(hoverNames[i], hoverValues[i]);
            }
            for (int i = 0; i < executeNames.length; i++) {
                JsonConverter.loopExecutes.put(executeNames[i], StringUtils.splitExecuteMods(executeValues[i]));
            }
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public static void setHelpHoversExecutes(String[] hoverNames, String[] hoverValues, String[] executeNames, String[] executeValues) {
        if (hoverNames.length == hoverValues.length && executeNames.length == executeValues.length) {
            for (int i = 0; i < hoverNames.length; i++) {
                JsonConverter.helpHovers.put(hoverNames[i], hoverValues[i]);
            }
            for (int i = 0; i < executeNames.length; i++) {
                JsonConverter.helpExecutes.put(executeNames[i], StringUtils.splitExecuteMods(executeValues[i]));
            }
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public static void setRawtextHoversExecutes(String[] hoverNames, String[] hoverValues, String[] executeNames, String[] executeValues) {
        if (hoverNames.length == hoverValues.length && executeNames.length == executeValues.length) {
            for (int i = 0; i < hoverNames.length; i++) {
                JsonConverter.rawtextHovers.put(hoverNames[i], hoverValues[i]);
            }
            for (int i = 0; i < executeNames.length; i++) {
                JsonConverter.rawtextExecutes.put(executeNames[i], StringUtils.splitExecuteMods(executeValues[i]));
            }
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public static String getJsonString(String chatText, boolean isForHelp, boolean isForRaw) {
        Map<String, String> hovers;
        Map<String, String[]> executes;
        if (isForHelp) {
            hovers = helpHovers;
            executes = helpExecutes;
        } else {
            hovers = loopHovers;
            executes = loopExecutes;
        }
        if (isForRaw) {
            hovers = rawtextHovers;
            executes = rawtextExecutes;
        }

        StringBuilder builder = new StringBuilder();
        ArrayList<String[]> source = StringUtils.translateTextParts(chatText);

        builder.append("\"\"");
        for (String[] item : source) {
            builder.append(",{");
            builder.append("\"text\":\"").append(item[0]).append("\"");
            if (!item[2].equals("") && (executes.containsKey(item[2]) || (isForHelp && item[2].equals("a")) || (item[2].matches("^.*\\d+$") && executes.containsKey(item[2].replaceAll("\\d+", ""))))) {
                builder.append(",\"clickEvent\":{");

                if (isForHelp && item[2].equals("a")) {
                    builder.append("\"action\":\"suggest_command\"");
                    builder.append(",");
                    builder.append("\"value\":\"").append(StringUtils.deleteColors(item[0])).append("\"");
                } else if (item[2].matches("^.*\\d+$") && executes.containsKey(item[2].replaceAll("\\d+", ""))) {
                    builder.append("\"action\":\"").append(StringUtils.getExecuteMod(executes.get(item[2].replaceAll("\\d+", ""))[0])).append("\"");
                    builder.append(",");

                    String number = "";
                    Matcher m = Pattern.compile("\\d+").matcher(item[2]);
                    while (m.find()) {
                        number = m.group();
                    }

                    builder.append("\"value\":\"").append(executes.get(item[2].replaceAll("\\d+", ""))[1]).append(" ").append(number).append("\"");
                } else {
                    builder.append("\"action\":\"").append(StringUtils.getExecuteMod(executes.get(item[2])[0])).append("\"");
                    builder.append(",");
                    builder.append("\"value\":\"").append(executes.get(item[2])[1]).append("\"");
                }

                builder.append("}");
            }

            if (!item[1].equals("") && hovers.containsKey(item[1])) {
                builder.append(",\"hoverEvent\":{");

                builder.append("\"action\":\"").append("show_text").append("\"");
                builder.append(",");
                builder.append("\"value\":\"").append(hovers.get(item[1])).append("\"");

                builder.append("}");
            }
            builder.append("}");
        }

        String result = builder.toString();
        result = "[" + result + "]";
        return result;
    }

    public static String getTextJsonString(String chatText) {
        return "[\"\",{\"text\":\"" + chatText + "\"}]";
    }

}
