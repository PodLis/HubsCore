package ru.hubsmc.hubscore;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import ru.hubsmc.hubscore.util.MessageUtils;
import java.util.List;
import java.util.logging.Level;

import static ru.hubsmc.hubscore.util.MessageUtils.*;
import static ru.hubsmc.hubscore.util.StringUtils.setPlaceholdersPrefixes;

public abstract class HubsCommand implements CommandExecutor, TabCompleter {

    private String name;
    private Permissions permission;
    private boolean mustBePlayer;
    private int minArgs;
    private String usage;

    private ConfigurationSection namespace;

    public HubsCommand(String name, Permissions permission, boolean mustBePlayer, int minArgs) {
        this.name = name;
        this.permission = permission;
        this.mustBePlayer = mustBePlayer;
        this.minArgs = minArgs;
    }

    @Override
    public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        namespace = MessageUtils.getCommandNamespace(name);
        usage = namespace.getString("usage");
        try {
            if (command.getName().equalsIgnoreCase(name)) {

                if (permission != null && !permission.senderHasPerm(sender)) {
                    sendNoPermMessage(sender, name);
                    return true;
                }

                if (args.length < minArgs) {
                    sendWrongUsageMessage(sender, usage);
                    return true;
                }

                if (mustBePlayer && !(sender instanceof Player)) {
                    sendMustBePlayerMessage(sender, name);
                    return true;
                } else if (mustBePlayer) {
                    Player player = (Player) sender;
                    return onHubsCommand(player, command, label, args);
                } else {
                    return onHubsCommand(sender, command, label, args);
                }

            }
        } catch (Throwable e) {
            e.printStackTrace();
            PluginUtils.logConsole(Level.WARNING, "Some troubles with command '" + name + "'.");
        }
        return true;
    }

    @Override
    public final List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (permission != null && !permission.senderHasPerm(sender)) {
            return null;
        }
        return onHubsComplete(sender, command, alias, args);
    }

    abstract public boolean onHubsCommand(CommandSender sender, Command command, String label, String[] args);

    abstract public List<String> onHubsComplete(CommandSender sender, Command command, String alias, String[] args);

    protected final void sendPlaceholderMessage(CommandSender sender, String path, String... data) {
        sendMessage(sender, setPlaceholdersPrefixes(getNamespaceString(namespace, path, "command-messages." + name), data));
    }

    protected final void sendDefaultUsage(CommandSender sender) {
        sendWrongUsageMessage(sender, usage);
    }

}
