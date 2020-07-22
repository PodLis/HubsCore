package ru.hubsmc.hubscore.module.security;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.hubsmc.hubscore.CoreModule;

import java.util.List;

public class HubsSecurity extends CoreModule {

    @Override
    public boolean onEnable() {
        return true;
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onPlayerJoin(Player player) {
    }

    @Override
    public void onPlayerLeave(Player player) {
    }

    @Override
    public void onSchedule(byte min) {
    }

    @Override
    public boolean onCommandExecute(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

}
