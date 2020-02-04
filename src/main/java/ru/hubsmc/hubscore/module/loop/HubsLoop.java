package ru.hubsmc.hubscore.module.loop;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import ru.hubsmc.hubscore.CoreModule;
import ru.hubsmc.hubscore.PluginUtils;
import ru.hubsmc.hubscore.module.loop.api.*;
import ru.hubsmc.hubscore.util.ConfigUtils;
import ru.hubsmc.hubscore.util.JsonConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ru.hubsmc.hubscore.util.StringUtils.replaceSymbolsAndNull;

public class HubsLoop extends CoreModule {

    private char loop_counter;
    private char bossBar_counter;
    private char actionBar_counter;
    private char chatMessage_counter;

    private int bossBar_mark;
    private int actionBar_mark;
    private int chatMessage_mark;
    private int feed_mark;

    private int actionBar_stand_seconds;
    private ActionBar currentActionBar;

    private ArrayList<HubsBar> bossBars;
    private ArrayList<ActionBar> actionBars;
    private ArrayList<ChatMessage> chatMessages;

    private PlayerCommand playerFeedCommand;
    private String consoleFeedCommand;

    @Override
    public void onEnable() {
        loadFiles();
    }

    @Override
    public void onDisable() {
        for (HubsBar bar : bossBars) {
            bar.clean();
        }
    }

    @Override
    public void onReload() {
        for (HubsBar bar : bossBars) {
            bar.clean();
        }
        loadFiles();
    }

    @Override
    public void onPlayerJoin(Player player) {
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

        if (loop_counter % feed_mark == 0) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), consoleFeedCommand);
            playerFeedCommand.send(players);
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
        feed_mark = configuration.getInt("periodic-feed.delay");

        //feeding
        consoleFeedCommand = configuration.getString("periodic-feed.cmd-to-console");
        playerFeedCommand = new PlayerCommand(configuration.getString("periodic-feed.cmd-to-player"));

        //boss-bars
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

        //action-bars
        String[] actionTexts = ConfigUtils.getStrings(configuration.getConfigurationSection("action-bars.bars"), "text");
        int[] actionStandTimes = ConfigUtils.getIntegers(configuration.getConfigurationSection("action-bars.bars"), "stand-time");
        actionBars = new ArrayList<>();
        for (int i = 0; i < actionTexts.length; i++) {
            actionBars.add(new ActionBar(replaceSymbolsAndNull(actionTexts[i]), actionStandTimes[i]));
        }

        //chat-messages
        String[][] chatTexts = ConfigUtils.getArrayOfStrings(configuration.getConfigurationSection("chat-messages.messages"), "text");
        boolean[] chatIsJson = ConfigUtils.getBooleans(configuration.getConfigurationSection("chat-messages.messages"), "raw");
        JsonConverter.setHoversExecutes(
                ConfigUtils.getStringsAndKeys(configuration.getConfigurationSection("chat-messages.hover"))[0],
                ConfigUtils.getStringsAndKeys(configuration.getConfigurationSection("chat-messages.hover"))[1],
                ConfigUtils.getStringsAndKeys(configuration.getConfigurationSection("chat-messages.execute"))[0],
                ConfigUtils.getStringsAndKeys(configuration.getConfigurationSection("chat-messages.execute"))[1]
        );
        chatMessages = new ArrayList<>();
        for (int i = 0; i < chatTexts.length; i++) {
            String[] strings = new String[chatTexts[i].length];
            if (chatIsJson[i]) {

                for (int j = 0; j < chatTexts[i].length; j++) {
                    strings[j] = replaceSymbolsAndNull(JsonConverter.getJsonString(chatTexts[i][j]));
                }
                chatMessages.add(new RawMessage(strings)); //Hover-Click-able messages

            } else {

                for (int j = 0; j < chatTexts[i].length; j++) {
                    strings[j] = replaceSymbolsAndNull(chatTexts[i][j]);
                }
                chatMessages.add(new ChatMessage(strings)); //Simple-text messages

            }
        }

    }

}
