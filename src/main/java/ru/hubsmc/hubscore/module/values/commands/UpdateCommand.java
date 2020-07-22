package ru.hubsmc.hubscore.module.values.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.hubsmc.hubscore.HubsCommand;
import ru.hubsmc.hubscore.PluginUtils;

import java.util.List;

public class UpdateCommand extends HubsCommand {

    public UpdateCommand() {
        super("update", null, true, 0);
    }

    @Override
    public boolean onHubsCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        PluginUtils.getHubsPlayer(player).updateNormalVars();
        return true;
    }

    @Override
    public List<String> onHubsComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }

}
