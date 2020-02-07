package ru.hubsmc.hubscore.module.loop.title;

public class HubsTitle {

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

}