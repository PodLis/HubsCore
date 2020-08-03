package su.hubs.hubscore.module.essentials.social

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import su.hubs.hubscore.HubsCommand
import su.hubs.hubscore.PluginUtils
import su.hubs.hubscore.exception.ServerErrorException
import su.hubs.hubscore.util.MessageUtils
import su.hubs.hubscore.util.ServerUtils
import java.io.IOException

class LobbyCommand : HubsCommand("lobby", null, true, 0) {

    override fun onHubsCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        val player = sender as Player
        try {
            try {
                if (ServerUtils.changeServer(player, "lobby")) {
                    MessageUtils.sendAlreadyThatServerMessage(player)
                }
            } catch (e: IOException) {
                throw ServerErrorException(PluginUtils.getBungeeServerName(), "lobby")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true
    }

    override fun onHubsComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>) = null
}