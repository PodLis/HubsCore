package ru.hubsmc.hubscore.module.donate;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.hubsmc.hubscore.CoreModule;
import ru.hubsmc.hubscore.module.donate.commands.CartCommand;
import ru.hubsmc.hubscore.module.donate.commands.DonateCommand;

import java.util.List;

import static ru.hubsmc.hubscore.PluginUtils.setCommandExecutorAndTabCompleter;

public class HubsDonate extends CoreModule {

    @Override
    public boolean onEnable() {
        setCommandExecutorAndTabCompleter(new DonateCommand());
        setCommandExecutorAndTabCompleter(new CartCommand());
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

    public static void buyDonate(Player player, String donateKey) {
    }

}
