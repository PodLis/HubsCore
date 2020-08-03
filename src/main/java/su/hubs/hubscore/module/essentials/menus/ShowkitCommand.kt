package su.hubs.hubscore.module.essentials.menus

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import su.hubs.hubscore.HubsCommand
import su.hubs.hubscore.module.chesterton.internal.parser.MenuParser
import su.hubs.hubscore.util.PlayerUtils

class ShowkitCommand : HubsCommand("showkit", null, true, 0, "kitshow") {
    override fun onHubsCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        PlayerUtils.openMenuToPlayer(sender as Player, MenuParser.parseChestMenu("kit_menu"))
        return true
    }

    override fun onHubsComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>) = null
}