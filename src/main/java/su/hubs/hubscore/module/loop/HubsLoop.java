package su.hubs.hubscore.module.loop;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import su.hubs.hubscore.CoreModule;
import su.hubs.hubscore.PluginUtils;
import su.hubs.hubscore.exception.ConfigurationPartMissingException;
import su.hubs.hubscore.module.loop.action.ActionBar;
import su.hubs.hubscore.module.loop.board.App;
import su.hubs.hubscore.module.loop.boss.HubsBar;
import su.hubs.hubscore.module.loop.chat.ChatListener;
import su.hubs.hubscore.module.loop.chat.ChatMessage;
import su.hubs.hubscore.module.loop.chat.RawMessage;
import su.hubs.hubscore.module.loop.chat.plugins.PermissionsPlugin;
import su.hubs.hubscore.module.loop.chat.plugins.PluginManager;
import su.hubs.hubscore.util.ConfigUtils;
import su.hubs.hubscore.util.JsonConverter;
import su.hubs.hubscore.util.PlayerUtils;
import su.hubs.hubscore.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    public boolean onEnable() {
        loadFiles();
        manager = new PluginManager();
        PluginUtils.logConsole("Successfully hooked into: " + PluginManager.getInstance().getName());
        PluginUtils.registerEventsOfListener(new ChatListener());
        return true;
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
        player.setPlayerListName(PlayerUtils.replacePlayerPlaceholders(player, TAB_FORMAT));
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
                    StringUtils.replaceSymbolsAndNull(bossTexts[i]),
                    BarColor.valueOf(bossColors[i]),
                    BarStyle.valueOf(bossStyles[i]),
                    bossProgresses[i]));
        }

        // action-bars
        String[] actionTexts = ConfigUtils.getStrings(configuration.getConfigurationSection("action-bars.bars"), "text");
        int[] actionStandTimes = ConfigUtils.getIntegers(configuration.getConfigurationSection("action-bars.bars"), "stand-time");
        actionBars = new ArrayList<>();
        for (int i = 0; i < actionTexts.length; i++) {
            actionBars.add(new ActionBar(StringUtils.replaceSymbolsAndNull(actionTexts[i]), actionStandTimes[i]));
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

                chatMessages.add(new RawMessage(chatTexts[i], false, false)); //Hover-Click-able messages

            } else {

                String[] strings = new String[chatTexts[i].length];
                for (int j = 0; j < chatTexts[i].length; j++) {
                    strings[j] = StringUtils.replaceSymbolsAndNull(chatTexts[i][j]);
                }
                chatMessages.add(new ChatMessage(strings)); //Simple-text messages

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

}
