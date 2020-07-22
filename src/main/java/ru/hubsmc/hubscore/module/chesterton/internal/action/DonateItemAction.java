package ru.hubsmc.hubscore.module.chesterton.internal.action;

import org.bukkit.entity.Player;
import ru.hubsmc.hubscore.module.donate.HubsDonate;

public class DonateItemAction extends ItemAction {

    private String donateKey;

    public DonateItemAction(String donateKey) {
        this.donateKey = donateKey;
    }

    @Override
    public void execute(Player player) {
        HubsDonate.buyDonate(player, donateKey);
    }

}
