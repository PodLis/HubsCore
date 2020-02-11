package ru.hubsmc.hubscore.module.loop.title;

import org.bukkit.entity.Player;
import ru.hubsmc.hubscore.PluginUtils;

import java.util.List;
import java.util.function.Predicate;

public class TitleTask {
    private final Player player;
    private final List<HubsTitle> titleFrames;
    private final Predicate<Player> isNeedToStop;
    private final boolean repeat;
    private final int repeatFrom;
    private final int lastFrame;
    private int currFrame = -1;

    TitleTask(Player player, List<HubsTitle> titleFrames, boolean repeat, int repeatFrom, Predicate<Player> isNeedToStop) {
        this.player = player;
        this.titleFrames = titleFrames;
        this.repeat = repeat;
        this.lastFrame = titleFrames.size() - 1;
        this.repeatFrom = repeatFrom;
        this.isNeedToStop = isNeedToStop;
        nextFrame();
    }

    private void nextFrame() {
        if (!player.isOnline()) return;
        if (isNeedToStop.test(player)) {
            player.sendTitle("", "", 20, 20, 20);
            return;
        }
        if (currFrame == lastFrame) {
            if (repeat) {
                currFrame = repeatFrom;
            } else {
                return;
            }
        }

        HubsTitle frame = titleFrames.get(++currFrame);

        player.sendTitle(frame.getTitle(), frame.getSubtitle(), frame.getFadeIn(), frame.getStay(), frame.getFadeOut());
        PluginUtils.runTaskLater(this::nextFrame, frame.getDelayNext());
    }

}