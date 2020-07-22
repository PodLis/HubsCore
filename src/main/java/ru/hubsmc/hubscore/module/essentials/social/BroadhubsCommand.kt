package ru.hubsmc.hubscore.module.essentials.social

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import ru.hubsmc.hubscore.HubsCommand
import ru.hubsmc.hubscore.Permissions
import ru.hubsmc.hubscore.util.StringUtils

class BroadhubsCommand : HubsCommand("broadhubs", Permissions.BROADHUBS, false, 0) {

    override fun onHubsCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        return true.also {Bukkit.getServer().broadcastMessage(StringUtils.replaceColor((StringBuilder().also {
            string -> args.forEach { string.append(it).append(" ") }
        }).toString()))}
    }

    override fun onHubsComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>) = null

}