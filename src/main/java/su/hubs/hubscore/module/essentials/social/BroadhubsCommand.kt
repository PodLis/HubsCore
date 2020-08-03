package su.hubs.hubscore.module.essentials.social

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import su.hubs.hubscore.HubsCommand
import su.hubs.hubscore.GlobalPermission
import su.hubs.hubscore.util.StringUtils

class BroadhubsCommand : HubsCommand("broadhubs", GlobalPermission.BROADHUBS, false, 0) {

    override fun onHubsCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        return true.also {Bukkit.getServer().broadcastMessage(StringUtils.replaceColor((StringBuilder().also {
            string -> args.forEach { string.append(it).append(" ") }
        }).toString()))}
    }

    override fun onHubsComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>) = null

}