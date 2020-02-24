package ru.hubsmc.hubscore.module.loop;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import ru.hubsmc.hubscore.CoreModule;
import ru.hubsmc.hubscore.PluginUtils;
import ru.hubsmc.hubscore.exception.ConfigurationPartMissingException;
import ru.hubsmc.hubscore.module.loop.action.ActionBar;
import ru.hubsmc.hubscore.module.loop.board.App;
import ru.hubsmc.hubscore.module.loop.boss.HubsBar;
import ru.hubsmc.hubscore.module.loop.chat.ChatListener;
import ru.hubsmc.hubscore.module.loop.chat.ChatMessage;
import ru.hubsmc.hubscore.module.loop.chat.RawMessage;
import ru.hubsmc.hubscore.module.loop.chat.plugins.PermissionsPlugin;
import ru.hubsmc.hubscore.module.loop.chat.plugins.PluginManager;
import ru.hubsmc.hubscore.util.ConfigUtils;
import ru.hubsmc.hubscore.util.JsonConverter;
import ru.hubsmc.hubscore.util.StringUtils;

import java.util.*;

import static ru.hubsmc.hubscore.PluginUtils.logConsole;
import static ru.hubsmc.hubscore.util.PlayerUtils.replacePlayerPlaceholders;
import static ru.hubsmc.hubscore.util.StringUtils.replaceSymbolsAndNull;

public class HubsLoop extends CoreModule {

    private char loop_counter;
    private char bossBar_counter;
    private char actionBar_counter;
    private char chatMessage_counter;

    private int bossBar_mark;
    private int actionBar_mark;
    private int chatMessage_mark;

    private int actionBar_stand_seconds;
    private ActionBar currentActionBar;

    private ArrayList<HubsBar> bossBars;
    private ArrayList<ActionBar> actionBars;
    private ArrayList<ChatMessage> chatMessages;

    private static Map<String, RawMessage> helpMessages;

    public static Scoreboard EMPTY_BOARD;
    public static App app;

    private static PluginManager manager;
    public static String NORMAL_FORMAT;
    public static String LOCAL_FORMAT;
    public static String GLOBAL_FORMAT;
    public static String TAB_FORMAT;
    public static double LOCAL_RANGE;
    public static List<String> goodIpsAndDomains;

    @Override
    public void onEnable() {
        loadFiles();
        manager = new PluginManager();
        PluginUtils.setCommandExecutorAndTabCompleter("help", new HelpCommand());
        logConsole("Successfully hooked into: " + PluginManager.getInstance().getName());
        PluginUtils.registerEventsOfListener(new ChatListener());
    }

    @Override
    public void onDisable() {
        unloadFiles();
    }

    @Override
    public void onReload() {
        unloadFiles();
        loadFiles();
    }

    @Override
    public void onPlayerJoin(Player player) {
        player.setPlayerListName(replacePlayerPlaceholders(player, TAB_FORMAT));
    }

    @Override
    public void onPlayerLeave(Player player) {
    }

    @Override
    public void onSchedule(byte min) {

        Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        if (loop_counter % bossBar_mark == 0) {
            bossBars.get(bossBar_counter % bossBars.size()).clean();
            bossBar_counter++;
            bossBars.get(bossBar_counter % bossBars.size()).send(players);
        }

        if (actionBar_stand_seconds > 0 && currentActionBar != null) {
            currentActionBar.send(players);
            actionBar_stand_seconds--;
        }

        if (loop_counter % actionBar_mark == 0) {
            actionBar_counter++;
            currentActionBar = actionBars.get(actionBar_counter % actionBars.size());
            currentActionBar.send(players);
            actionBar_stand_seconds = currentActionBar.getExtraDisplayTime();
        }

        if (loop_counter % chatMessage_mark == 0) {
            chatMessage_counter++;
            chatMessages.get(chatMessage_counter % chatMessages.size()).send(players);
        }

        loop_counter++;

    }

    @Override
    public boolean onCommandExecute(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

    private void unloadFiles() {
        app.cancel();
        for (HubsBar bar : bossBars) {
            bar.clean();
        }
    }

    private void loadFiles() {

        FileConfiguration configuration = PluginUtils.getConfigInCoreFolder("loop");

        loop_counter = 0;
        bossBar_counter = Character.MAX_VALUE;
        actionBar_counter = Character.MAX_VALUE;
        chatMessage_counter = Character.MAX_VALUE;

        actionBar_stand_seconds = 0;
        currentActionBar = null;

        bossBar_mark = configuration.getInt("boss-bars.delay");
        actionBar_mark = configuration.getInt("action-bars.delay");
        chatMessage_mark = configuration.getInt("chat-messages.delay");

        // boss-bars
        String[] bossTexts = ConfigUtils.getStrings(configuration.getConfigurationSection("boss-bars.bars"), "text");
        String[] bossColors = ConfigUtils.getStrings(configuration.getConfigurationSection("boss-bars.bars"), "color");
        String[] bossStyles = ConfigUtils.getStrings(configuration.getConfigurationSection("boss-bars.bars"), "segmented");
        double[] bossProgresses = ConfigUtils.getDoubles(configuration.getConfigurationSection("boss-bars.bars"), "progress");
        bossBars = new ArrayList<>();
        for (int i = 0; i < bossTexts.length; i++) {
            bossBars.add(new HubsBar(
                    replaceSymbolsAndNull(bossTexts[i]),
                    BarColor.valueOf(bossColors[i]),
                    BarStyle.valueOf(bossStyles[i]),
                    bossProgresses[i]));
        }

        // action-bars
        String[] actionTexts = ConfigUtils.getStrings(configuration.getConfigurationSection("action-bars.bars"), "text");
        int[] actionStandTimes = ConfigUtils.getIntegers(configuration.getConfigurationSection("action-bars.bars"), "stand-time");
        actionBars = new ArrayList<>();
        for (int i = 0; i < actionTexts.length; i++) {
            actionBars.add(new ActionBar(replaceSymbolsAndNull(actionTexts[i]), actionStandTimes[i]));
        }

        // chat-messages
        String[][] chatTexts = ConfigUtils.getArrayOfStrings(configuration.getConfigurationSection("chat-messages.messages"), "text");
        boolean[] chatIsJson = ConfigUtils.getBooleans(configuration.getConfigurationSection("chat-messages.messages"), "raw");
        JsonConverter.setLoopHoversExecutes(
                ConfigUtils.getStringsAndKeys(configuration.getConfigurationSection("chat-messages.hover"))[0],
                ConfigUtils.getStringsAndKeys(configuration.getConfigurationSection("chat-messages.hover"))[1],
                ConfigUtils.getStringsAndKeys(configuration.getConfigurationSection("chat-messages.execute"))[0],
                ConfigUtils.getStringsAndKeys(configuration.getConfigurationSection("chat-messages.execute"))[1]
        );
        chatMessages = new ArrayList<>();
        for (int i = 0; i < chatTexts.length; i++) {
            if (chatIsJson[i]) {

                chatMessages.add(new RawMessage(chatTexts[i], false)); //Hover-Click-able messages

            } else {

                String[] strings = new String[chatTexts[i].length];
                for (int j = 0; j < chatTexts[i].length; j++) {
                    strings[j] = replaceSymbolsAndNull(chatTexts[i][j]);
                }
                chatMessages.add(new ChatMessage(strings)); //Simple-text messages

            }
        }

        // help-messages
        FileConfiguration helpConfiguration = PluginUtils.getConfigInCoreFolder("help");
        JsonConverter.setHelpHoversExecutes(
                ConfigUtils.getStringsAndKeys(helpConfiguration.getConfigurationSection("hover"))[0],
                ConfigUtils.getStringsAndKeys(helpConfiguration.getConfigurationSection("hover"))[1],
                ConfigUtils.getStringsAndKeys(helpConfiguration.getConfigurationSection("execute"))[0],
                ConfigUtils.getStringsAndKeys(helpConfiguration.getConfigurationSection("execute"))[1]
        );
        helpMessages = new HashMap<>();
        for (String key : helpConfiguration.getKeys(false)) {
            if (key.equals("hover") || key.equals("execute"))
                continue;
            if (key.equals("menu")) {
                helpMessages.put(key, new RawMessage(StringUtils.listOfStringsToStringsArray(helpConfiguration.getStringList(key)), true));
            } else {
                for (String subKey : helpConfiguration.getConfigurationSection(key).getKeys(false)) {
                    if (subKey.equals("menu")) {
                        helpMessages.put(key, new RawMessage(StringUtils.listOfStringsToStringsArray(helpConfiguration.getStringList(key + "." + subKey)), true));
                    }
                    helpMessages.put(key + " " + subKey, new RawMessage(StringUtils.listOfStringsToStringsArray(helpConfiguration.getStringList(key + "." + subKey)), true));
                }
            }
        }

        // scoreboards
        EMPTY_BOARD = PluginUtils.createScoreboard();
        ConfigurationSection section;
        try {
            section = configuration.getConfigurationSection("board");
            if (section == null) {
                throw new ConfigurationPartMissingException("'board' section is not exist in 'loop.yml'");
            }
            app = new App(section);
        } catch (ConfigurationPartMissingException e) {
            e.printStackTrace();
        }
        PluginUtils.runAppTaskTimer(app);

        // player-chat and tab
        NORMAL_FORMAT = PluginUtils.getStringsConfig().getString("chat.format.normal");
        LOCAL_FORMAT = PluginUtils.getStringsConfig().getString("chat.format.local");
        GLOBAL_FORMAT = PluginUtils.getStringsConfig().getString("chat.format.global");
        TAB_FORMAT = PluginUtils.getStringsConfig().getString("tab.format");
        LOCAL_RANGE = configuration.getDouble("chat.local-range");
        goodIpsAndDomains = configuration.getStringList("chat.good-ips-and-domains");

    }

    public static PermissionsPlugin getChatManager() {
        return manager;
    }

    public static void sendHelpMessage(String key, Player player) {
        if (helpMessages.containsKey(key)) helpMessages.get(key).send(player);
        else helpMessages.get("menu").send(player);
    }

    public static Set<String> getHelpMessageNames() {
        return helpMessages.keySet();
    }

}
