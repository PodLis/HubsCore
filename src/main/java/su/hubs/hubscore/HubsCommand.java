package su.hubs.hubscore;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import su.hubs.hubscore.exception.ConfigurationPartMissingException;
import su.hubs.hubscore.util.MessageUtils;
import su.hubs.hubscore.util.StringUtils;

import java.util.List;
import java.util.logging.Level;

import static su.hubs.hubscore.module.values.api.API.*;

public abstract class HubsCommand implements CommandExecutor, TabCompleter {

    private final String name;
    private final HubsPermission permission;
    private final boolean mustBePlayer;
    private final int minArgs;
    private String usage;
    private int manaCost = 0;
    private int dollarsCost = 0;
    private int cooldown = 0;
    private int waitTime = 0;

    private ConfigurationSection namespace;

    public HubsCommand(String name, HubsPermission permission, boolean mustBePlayer, int minArgs, String... aliases) {
        this(null, name, permission, mustBePlayer, minArgs, aliases);
    }

    public HubsCommand(HubsServer owner, String name, HubsPermission permission, boolean mustBePlayer, int minArgs, String... aliases) {
        PluginUtils.registerCommand(HubsCore.getInstance(), name, permission, aliases);
        this.name = name;
        this.permission = permission;
        this.mustBePlayer = mustBePlayer;
        this.minArgs = minArgs;
        ConfigurationSection manaConfig;
        if (owner == null) {
            try {
                namespace = MessageUtils.getCommandNamespace(name);
                if (namespace == null) {
                    throw new ConfigurationPartMissingException("usage of the HubsCommand '" + name + "' is missing in strings.yml");
                }
                usage = namespace.getString("usage");
            } catch (ConfigurationPartMissingException e) {
                e.printStackTrace();
            }
            manaConfig = PluginUtils.getConfigInCoreFolder("mana").getConfigurationSection("commands." + name);
        } else {
            try {
                namespace = MessageUtils.getServerCommandNamespace(name, owner);
                if (namespace == null) {
                    throw new ConfigurationPartMissingException("usage of the HubsCommand '" + name + "' is missing in strings.yml of " + owner.getStringData("folder"));
                }
                usage = namespace.getString("usage");
            } catch (ConfigurationPartMissingException e) {
                e.printStackTrace();
            }
            manaConfig = PluginUtils.getConfigInServerFolder("mana", owner).getConfigurationSection("commands." + name);
        }
        if (manaConfig != null) {
            manaCost = manaConfig.getInt("mana", 0);
            dollarsCost = manaConfig.getInt("dollars", 0);
            cooldown = manaConfig.getInt("cd", 0);
            waitTime = manaConfig.getInt("wait", 0);
        }
    }

    @Override
    public final boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

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
                    if (manaCost > 0 && getMana(player) < manaCost) {
                        MessageUtils.sendNotEnoughManaMessage(player, manaCost);
                        return true;
                    }
                    if (dollarsCost > 0 && getDollars(player) < dollarsCost) {
                        MessageUtils.sendNotEnoughDollarsMessage(player, dollarsCost);
                        return true;
                    }
                    if (cooldown > 0) {
                        if (PluginUtils.getHubsPlayer(player).hasStatus(name)) {
                            MessageUtils.sendCooldownMessage(player, 60000);
                            return true;
                        }
                        long time = HubsCore.getCooldownTime(player.getUniqueId().toString(), name);
                        long now = System.currentTimeMillis();
                        if (time + cooldown > now) {
                            MessageUtils.sendCooldownMessage(player, time + cooldown - now);
                            return true;
                        }
                    }
                    if (waitTime <= 0) {
                        if (onHubsCommand(player, command, label, args)) {
                            if (manaCost > 0)
                                removeMana(player, manaCost);
                            if (dollarsCost > 0)
                                removeDollars(player, dollarsCost);
                            if (cooldown > 0)
                                if (cooldown > 60)
                                    HubsCore.setCooldownTime(player.getUniqueId().toString(), name);
                                else
                                    PluginUtils.getHubsPlayer(player).addTempStatus(name, cooldown);
                        }
                    } else {
                        MessageUtils.sendPleaseWaitMessage(player, waitTime);
                        Location location = player.getLocation();
                        BukkitTask task = PluginUtils.runTaskLater(
                                () -> {
                                    if (onHubsCommand(player, command, label, args)) {
                                        if (manaCost > 0)
                                            removeMana(player, manaCost);
                                        if (dollarsCost > 0)
                                            removeDollars(player, dollarsCost);
                                        if (cooldown > 0)
                                            if (cooldown > 60)
                                                HubsCore.setCooldownTime(player.getUniqueId().toString(), name);
                                            else
                                                PluginUtils.getHubsPlayer(player).addTempStatus(name, cooldown);
                                    }
                                },
                                waitTime * 20
                        );
                        BukkitTask bukkitTask = PluginUtils.runTaskTimer(
                                () -> {
                                    if (player.getLocation().distanceSquared(location) > 0.5 && PluginUtils.isQueued(task)) {
                                        PluginUtils.cancelTask(task);
                                        MessageUtils.sendImpatientPlayerMessage(player);
                                    }
                                },
                                1, 2
                        );
                        PluginUtils.runTaskLater(
                                () -> PluginUtils.cancelTask(bukkitTask),
                                waitTime * 20 - 3
                        );
                        return true;
                    }
                    return true;
                } else {
                    if (!onHubsCommand(sender, command, label, args))
                        sendDefaultUsage(sender);
                    return true;
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
