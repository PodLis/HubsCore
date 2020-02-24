package ru.hubsmc.hubscore.module.chesterton.internal.action;

import org.bukkit.entity.Player;

public class CommandExecuteAction extends ItemAction {

    private String command;

    public CommandExecuteAction(String command) {
        this.command = command;
    }

    @Override
    public void execute(Player player) {
        player.performCommand(command);
    }

}
