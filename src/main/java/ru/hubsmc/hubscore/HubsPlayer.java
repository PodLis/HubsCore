package ru.hubsmc.hubscore;

import org.bukkit.entity.Player;
import ru.hubsmc.hubscore.module.loop.HubsLoop;
import ru.hubsmc.hubscore.module.loop.board.ScoreboardHolder;

public class HubsPlayer {

    private Player player;
    private ScoreboardHolder scoreboardHolder;
    private int dollars, mana, max, regen;

    HubsPlayer(Player player, int dollars, int mana, int max, int regen) {
        this.player = player;
        this.dollars = dollars;
        this.mana = mana;
        this.max = max;
        this.regen = regen;
        scoreboardHolder = new ScoreboardHolder(HubsLoop.app, this.player, dollars, mana, max, regen);
    }

    void onRemove() {
        HubsLoop.app.unregisterHolder(scoreboardHolder);
        player.setScoreboard(HubsLoop.EMPTY_BOARD);
    }

    public void updateNormalVars() {
        scoreboardHolder.updateValues();
    }

    public void updateCustomVars(String v1, String v2, String v3, String v4) {
        scoreboardHolder.updateCustomValues(v1, v2, v3, v4);
    }

    public void setValues(int dollars, int mana, int max, int regen) {
        this.dollars = dollars;
        this.mana = mana;
        this.max = max;
        this.regen = regen;
    }

    public int getDollars() {
        return dollars;
    }

    public void setDollars(int dollars) {
        this.dollars = dollars;
        updateNormalVars();
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
        updateNormalVars();
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
        updateNormalVars();
    }

    public int getRegen() {
        return regen;
    }

    public void setRegen(int regen) {
        this.regen = regen;
        updateNormalVars();
    }

}
