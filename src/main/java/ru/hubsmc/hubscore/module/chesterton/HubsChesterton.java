package ru.hubsmc.hubscore.module.chesterton;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import ru.hubsmc.hubscore.CoreModule;
import ru.hubsmc.hubscore.Permissions;
import ru.hubsmc.hubscore.module.chesterton.event.InventoryEvent;
import ru.hubsmc.hubscore.module.chesterton.internal.ChestertonInventoryHolder;
import ru.hubsmc.hubscore.module.chesterton.internal.MenuUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru.hubsmc.hubscore.PluginUtils.registerEventsOfListener;
import static ru.hubsmc.hubscore.util.MessageUtils.*;
import static ru.hubsmc.hubscore.util.ServerUtils.playerIsOnline;

public class HubsChesterton extends CoreModule {

    @Override
    public void onEnable() {
        registerEventsOfListener(new InventoryEvent());
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onReload() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getInventory().getHolder() instanceof ChestertonInventoryHolder) {
                player.closeInventory();
            }
        }
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
    public boolean onCommandExecute(CommandSender sender, Command command, String label, String[] oldArgs) {

        String[] args = new String[oldArgs.length - 2];
        System.arraycopy(oldArgs, 2, args, 0, args.length);

        if (args.length < 1) {
            sendWrongUsageMessage(sender, "/hc module HubsChesterton <sub_command>");
            return true;
        }

        if (args[0].equalsIgnoreCase("open")) {
            if (!Permissions.CHESTERTON_OPEN.senderHasPerm(sender)) {
                sendNoPermMessage(sender, args[0]);
                return true;
            }

            if (args.length < 2) {
                sendWrongUsageMessage(sender, "open <menu> [player]");
                return true;
            }

            if (!MenuUtils.menuExists(args[1])) {
                sendPrefixMessage(sender, "Данного меню не существует!");
                return true;
            }

            if (args.length == 2) {
                if (!(sender instanceof Player)) {
                    sendMustBePlayerMessage(sender, args[0]);
                    return true;
                }
                MenuUtils.openMenu((Player) sender, args[1]);
                return true;
            }

            if (!playerIsOnline(args[2])) {
                sendPlayerMustBeOnlineMessage(sender, args[2], args[0]);
                return true;
            }
            MenuUtils.openMenu(Bukkit.getPlayer(args[2]), args[1]);
            return true;
        }
        sendUnknownCommandMessage(sender, args[0]);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] oldArgs) {

        String[] args = new String[oldArgs.length - 2];
        System.arraycopy(oldArgs, 2, args, 0, args.length);

        List<String> completionList = new ArrayList<>();
        String partOfCommand;
        List<String> cmds = new ArrayList<>();

        switch (args.length) {
            case 1:
                cmds = new ArrayList<>(Collections.singletonList("open"));
                partOfCommand = args[0];

                StringUtil.copyPartialMatches(partOfCommand, cmds, completionList);
                Collections.sort(completionList);
                return completionList;

            case 2:
                if (args[0].equalsIgnoreCase("open")) {
                    cmds.addAll(MenuUtils.getAllMenuSet());
                    partOfCommand = args[1];
                    StringUtil.copyPartialMatches(partOfCommand, cmds, completionList);
                    Collections.sort(completionList);
                    return completionList;
                } else {
                    return null;
                }

            case 3:
                if (args[0].equalsIgnoreCase("open")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        cmds.add(player.getName());
                    }
                    partOfCommand = args[2];
                    StringUtil.copyPartialMatches(partOfCommand, cmds, completionList);
                    Collections.sort(completionList);
                    return completionList;
                } else {
                    return null;
                }

            default:
                return null;
        }

    }

}
