package su.hubs.hubscore.module.essentials.info

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import su.hubs.hubscore.HubsCommand
import su.hubs.hubscore.module.essentials.HubsEssentials

class DiscordCommand : HubsCommand("discord", null, true, 0, "ds") {

    override fun onHubsCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        HubsEssentials.sendRawtextMessage("discord", sender as Player)
        return true
    }

    override fun onHubsComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>) = null
}