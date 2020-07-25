package su.hubs.hubscore.module.loop.chat;

import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.entity.Player;
import su.hubs.hubscore.util.JsonConverter;
import su.hubs.hubscore.util.StringUtils;

import java.util.Collection;

public class RawMessage extends ChatMessage {

    public RawMessage(String[] lines, boolean isForHelp, boolean isForRaw) {
        super(prepareLines(lines, isForHelp, isForRaw));
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

    private static String[] prepareLines(String[] strings, boolean isForHelp, boolean isForRaw) {
        String[] newStrings = new String[strings.length];
        for (int j = 0; j < newStrings.length; j++) {
            if (strings[j].startsWith("<"))
                newStrings[j] = StringUtils.replaceSymbolsAndNull(JsonConverter.getJsonString(strings[j], isForHelp, isForRaw));
            else newStrings[j] = StringUtils.replaceSymbolsAndNull(JsonConverter.getTextJsonString(strings[j]));
        }
        return newStrings;
    }

}
