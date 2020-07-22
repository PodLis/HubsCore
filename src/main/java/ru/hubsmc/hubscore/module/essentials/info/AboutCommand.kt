package ru.hubsmc.hubscore.module.essentials.info

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.hubsmc.hubscore.HubsCommand
import ru.hubsmc.hubscore.module.essentials.HubsEssentials

class AboutCommand : HubsCommand("about", null, true, 0) {

    override fun onHubsCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        HubsEssentials.sendRawtextMessage("about", sender as Player)
        return true
    }

    override fun onHubsComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>) = null
}