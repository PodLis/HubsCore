package ru.hubsmc.hubscore;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import ru.hubsmc.hubscore.module.values.HubsValues;
import ru.hubsmc.hubscore.util.MessageUtils;
import java.util.List;
import java.util.logging.Level;

import static ru.hubsmc.hubscore.module.values.api.API.*;
import static ru.hubsmc.hubscore.module.values.api.API.getDollars;
import static ru.hubsmc.hubscore.util.MessageUtils.*;
import static ru.hubsmc.hubscore.util.MessageUtils.sendPrefixMessage;

public abstract class HubsCommand implements CommandExecutor, TabCompleter {

    @Override
    public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ConfigurationSection namespace = MessageUtils.getCommandNamespace("convert");
        try {
            if (command.getName().equalsIgnoreCase("convert")) {

                if (!(sender instanceof Player)) {
                    sendPrefixMessage(sender, "Converter must be a player");
                    return true;
                }

                Player player = (Player) sender;

                if (!Permissions.CONVERT.senderHasPerm(player)) {
                    sendNoPermMessage(player, label);
                    return true;
                }
                if (args.length < 1) {
                    sendNotEnoughArgsMessage(player, label);
                    return true;
                }

                int amount;
                try {
                    amount = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    sendPrefixMessage(player, "Мы не переводчики, чтобы переводить буквы!");
                    return true;
                }

                if (!takeHubixes(player, amount)){
                    sendPrefixMessage(player, "Ты хочешь перевести больше, чем у тебя есть!");
                    return true;
                }

                int rate = HubsValues.HUBIXES_TO_DOLLARS_RATE;

                if (rate <= 0) {
                    sendPrefixMessage(player, "Произошла непредвиденная ошибка! Пожалуйста, обратитесь к Администрации");
                    PluginUtils.logConsole(Level.WARNING, "Invalid configuration (HUBIXES_TO_DOLLARS_RATE <= 0)");
                    return true;
                }

                addDollars(player, amount * rate);

                sendPrefixMessage(player, "Ты перевёл &6" + amount + "Ⓗ&f в &e" + amount*rate + "$&f.");
                sendPrefixMessage(player, "Теперь у тебя &6" + getHubixes(player) + "Ⓗ&f и &e" + getDollars(player) + "$&f.");

                return true;

            }
        } catch (Throwable e) {
            e.printStackTrace();
            PluginUtils.logConsole(Level.WARNING, "Some troubles with command 'convert'.");
        }
        return true;
    }

    @Override
    public final List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return onHubsComplete(sender, command, alias, args);
    }

    abstract public boolean onHubsCommand(CommandSender sender, Command command, String label, String[] args);

    abstract public List<String> onHubsComplete(CommandSender sender, Command command, String alias, String[] args);

}
