package ru.hubsmc.hubscore;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum Permissions {

    RELOAD("hubscore.reload"),
    UTILS("hubscore.utils"),
    VALUE_CHECK("hubscore.value.check"),
    VALUE_CHANGE("hubscore.value.change"),
    CHESTERTON_OPEN("hubscore.chesterton.open"),
    PAY_NORMAL("hubs.pay.normal"),
    PAY_ALL("hubs.pay.all"),
    CONVERT("hubs.convert"),
    TOP("hubs.top");

    private final String perm;

    Permissions(String perm) {
        this.perm = perm;
    }

    public boolean senderHasPerm(CommandSender sender) {
        if (sender instanceof Player) {
            return sender.hasPermission(this.perm);
        }
        return true;
    }

}
