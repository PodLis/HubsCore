package su.hubs.hubscore.module.essentials.survival

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import su.hubs.hubscore.HubsCommand
import su.hubs.hubscore.util.MessageUtils

class KitCommand : HubsCommand("kit", null, true, 0) {
    override fun onHubsCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        MessageUtils.sendOnlyThatServerMessage(sender, "survival")
        return true
    }

    override fun onHubsComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>) = null
}