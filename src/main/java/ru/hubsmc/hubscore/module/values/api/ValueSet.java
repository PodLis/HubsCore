package ru.hubsmc.hubscore.module.values.api;

public class ValueSet {

    private int dollars, mana, max, regen;

    public ValueSet(int dollars, int mana, int max, int regen) {
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
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getRegen() {
        return regen;
    }

    public void setRegen(int regen) {
        this.regen = regen;
    }

}
