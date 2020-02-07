package ru.hubsmc.hubscore.module.loop.title;

import org.bukkit.entity.Player;
import ru.hubsmc.hubscore.exception.IncorrectConfigurationException;
import ru.hubsmc.hubscore.util.ConfigUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class TitleAnimation {

    private List<HubsTitle> titleFrames = new ArrayList<>();
    private boolean repeat;
    private int repeatFrom;

    public TitleAnimation(List<String> animSTitleList, boolean repeat, int repeatFrom) throws IncorrectConfigurationException {
        this.repeat = repeat;
        this.repeatFrom = repeatFrom;
        for (String line : animSTitleList) {
            titleFrames.add(ConfigUtils.parseTitle(line));
        }
    }

    public void play(Player player, Predicate<Player> isNeedToStop) {
        new TitleTask(player, titleFrames, repeat, repeatFrom, isNeedToStop);
    }

}