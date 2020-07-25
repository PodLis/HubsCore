package su.hubs.hubscore.module.chesterton

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import su.hubs.hubscore.HubsCommand
import su.hubs.hubscore.util.PlayerUtils

class MenuCommand : HubsCommand("menu", null, true, 0) {

    override fun onHubsCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        PlayerUtils.openMenuToPlayer(sender as Player, HubsChesterton.getNavigationMenu(sender))
        return true
    }

    override fun onHubsComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>) = null
}