package ru.hubsmc.hubscore.module.loop.chat;

import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.entity.Player;
import ru.hubsmc.hubscore.util.JsonConverter;

import java.util.Collection;

import static ru.hubsmc.hubscore.util.StringUtils.replaceSymbolsAndNull;

public class RawMessage extends ChatMessage {

    public RawMessage(String[] lines, boolean isForHelp) {
        super(prepareLines(lines, isForHelp));
    }

    @Override
    public void send(Collection<? extends Player> players) {
        for (Player player : players) {
            for (String line : lines) {
                try {
                    player.spigot().sendMessage(ComponentSerializer.parse(line));
                } catch (Throwable e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    @Override
    public void send(Player player) {
        for (String line : lines) {
            try {
                player.spigot().sendMessage(ComponentSerializer.parse(line));
            } catch (Throwable e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private static String[] prepareLines(String[] strings, boolean isForHelp) {
        String[] newStrings = new String[strings.length];
        for (int j = 0; j < newStrings.length; j++) {
            if (strings[j].startsWith("<"))
                newStrings[j] = replaceSymbolsAndNull(JsonConverter.getJsonString(strings[j], isForHelp));
            else newStrings[j] = replaceSymbolsAndNull(JsonConverter.getTextJsonString(strings[j]));
        }
        return newStrings;
    }

}
