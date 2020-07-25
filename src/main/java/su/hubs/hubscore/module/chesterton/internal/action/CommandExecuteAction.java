package su.hubs.hubscore.module.chesterton.internal.action;

import org.bukkit.entity.Player;
import su.hubs.hubscore.module.chesterton.internal.item.ChestertonItem;

public class CommandExecuteAction extends ItemAction {

    private String command;

    public CommandExecuteAction(String command) {
        this.command = command;
    }

    @Override
    public void execute(Player player, ChestertonItem item) {
        player.performCommand(command);
    }

}
