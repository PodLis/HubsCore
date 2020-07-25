package su.hubs.hubscore.module.essentials.info

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import su.hubs.hubscore.HubsCommand
import su.hubs.hubscore.module.essentials.HubsEssentials.Companion.getHelpMessageNames
import su.hubs.hubscore.module.essentials.HubsEssentials.Companion.sendHelpMessage
import java.util.*

class HelpCommand : HubsCommand("help", null, true, 0) {

    override fun onHubsCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        val player = sender as Player
        if (args.isEmpty()) {
            sendHelpMessage("menu", player)
        } else {
            val key = StringBuilder()
            for (keyPart in args) {
                key.append(" ").append(keyPart)
            }
            sendHelpMessage(key.toString().replaceFirst("\\s".toRegex(), ""), player)
        }
        return true
    }

    override fun onHubsComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        val completionList: MutableCollection<String> = ArrayList()
        val partOfCommand: String
        val cmds: List<String> = ArrayList(getHelpMessageNames())
        if (args.size == 1) {
            partOfCommand = args[0]
            StringUtil.copyPartialMatches(partOfCommand, cmds, completionList)
            return completionList.sorted()
        }
        return null
    }
}