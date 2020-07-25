package su.hubs.hubscore.module.essentials.info

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import su.hubs.hubscore.HubsCommand
import su.hubs.hubscore.Permissions
import su.hubs.hubscore.module.essentials.HubsEssentials

class KitsCommand : HubsCommand("kits", null, true, 0) {

    override fun onHubsCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (Permissions.STUFF_VIEW.senderHasPerm(sender as Player))
            HubsEssentials.sendRawtextMessage("stuff-kits", sender)
        else
            HubsEssentials.sendRawtextMessage("kits", sender)
        return true
    }

    override fun onHubsComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>) = null
}