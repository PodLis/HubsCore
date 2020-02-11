package ru.hubsmc.hubscore.module.loop.title;

import org.bukkit.entity.Player;
import ru.hubsmc.hubscore.module.loop.ToPlayerSendable;

import java.util.Collection;

public class HubsTitle implements ToPlayerSendable {

    private final String title;
    private final String subtitle;
    private final int fadeIn;
    private final int stay;
    private final int fadeOut;
    private final int delayNext;

    public HubsTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut, int delayNext) {
        this.title = title;
        this.subtitle = subtitle;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
        this.delayNext = delayNext;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public int getFadeIn() {
        return fadeIn;
    }

    public int getStay() {
        return stay;
    }

    public int getFadeOut() {
        return fadeOut;
    }

    public int getDelayNext() {
        return delayNext;
    }

    @Override
    public void send(Collection<? extends Player> players) {
        for (Player player : players) {
            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        }
    }

    @Override
    public void send(Player player) {
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

}