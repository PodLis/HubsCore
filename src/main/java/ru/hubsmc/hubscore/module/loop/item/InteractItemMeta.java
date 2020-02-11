package ru.hubsmc.hubscore.module.loop.item;

import org.bukkit.event.block.Action;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class InteractItemMeta {

    /**
     * hand/shift:
     *  1 - right/shift
     * -1 - left/no-shift
     *  0 - both/both
     */

    private ItemMeta itemMeta;
    private boolean hand;
    private boolean shift;

    public InteractItemMeta(ItemMeta itemMeta, Action action, boolean shift) {
        this.itemMeta = itemMeta;
        this.hand = action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK);
        this.shift = shift;
    }

    public InteractItemMeta(ItemMeta itemMeta, boolean hand, boolean shift) {
        this.itemMeta = itemMeta;
        this.hand = hand;
        this.shift = shift;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InteractItemMeta that = (InteractItemMeta) o;
        return hand == that.hand &&
                itemMeta.equals(that.itemMeta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemMeta, hand);
    }

}
