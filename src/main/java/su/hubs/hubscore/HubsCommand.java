package su.hubs.hubscore;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.hubs.hubscore.exception.ConfigurationPartMissingException;
import su.hubs.hubscore.util.MessageUtils;
import su.hubs.hubscore.util.StringUtils;

import java.util.List;
import java.util.logging.Level;

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
    public final boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            namespace = MessageUtils.getCommandNamespace(name);
            if (namespace == null) {
                throw new ConfigurationPartMissingException("usage of the HubsCommand '" + name + "' is missing in strings.yml");
            }
            usage = namespace.getString("usage");
        } catch (ConfigurationPartMissingException e) {
            e.printStackTrace();
        }

        try {
            if (command.getName().equalsIgnoreCase(name)) {

                if (permission != null && !permission.senderHasPerm(sender)) {
                    MessageUtils.sendNoPermMessage(sender, name);
                    return true;
                }

                if (args.length < minArgs) {
                    MessageUtils.sendWrongUsageMessage(sender, usage);
                    return true;
                }

                if (mustBePlayer && !(sender instanceof Player)) {
                    MessageUtils.sendMustBePlayerMessage(sender, name);
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
    public final List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (permission != null && !permission.senderHasPerm(sender)) {
            return null;
        }
        return onHubsComplete(sender, command, alias, args);
    }

    abstract public boolean onHubsCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args);

    abstract public List<String> onHubsComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args);

    protected final void sendPlaceholderMessage(CommandSender sender, String path, String... data) {
        MessageUtils.sendMessage(sender, StringUtils.setPlaceholdersPrefixes(MessageUtils.getNamespaceString(namespace, path, "command-messages." + name), data));
    }

    protected final void sendDefaultUsage(CommandSender sender) {
        MessageUtils.sendWrongUsageMessage(sender, usage);
    }

    public String getName() {
        return name;
    }

}
