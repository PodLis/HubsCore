package su.hubs.hubscore;

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
    TOP("hubs.top"),
    CHAT_ALLOW("hubs.chat.allow"),
    CHAT_GLOBAL("hubs.chat.global"),
    CHAT_BYPASS("hubs.chat.bypass"),
    CHAT_COLOR("hubs.chat.color"),
    CHAT_MAGIC("hubs.chat.magic"),
    CHAT_BOLD("hubs.chat.bold"),
    CHAT_STRIKETHROUGH("hubs.chat.strikethrough"),
    CHAT_UNDERLINE("hubs.chat.underline"),
    CHAT_ITALIC("hubs.chat.italic"),
    CHAT_RESET("hubs.chat.reset"),
    BONUS("hubs.bonus"),
    BROADHUBS("hubs.broadhubs"),
    RABBIT("hubs.rabbit"),
    VISION("hubs.vision"),
    STUFF_VIEW("hubs.stuff");

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
