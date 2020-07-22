package ru.hubsmc.hubscore;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

abstract public class CoreModule {

    abstract public boolean onEnable();

    abstract public void onDisable();

    abstract public void onReload();

    abstract public void onPlayerJoin(Player player);

    abstract public void onPlayerLeave(Player player);

    abstract public void onSchedule(byte min);

    abstract public boolean onCommandExecute(CommandSender sender, Command command, String label, String[] args);

    abstract public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args);

}
