package su.hubs.hubscore.module.essentials.abilities

import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import su.hubs.hubscore.HubsCommand
import su.hubs.hubscore.Permissions
import su.hubs.hubscore.PluginUtils

class RabbitCommand : HubsCommand("rabbit", Permissions.RABBIT, true, 0) {

    override fun onHubsCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        val hubsPlayer = PluginUtils.getHubsPlayer(sender as Player)
        if (hubsPlayer.hasStatus("rabbit"))
            sendPlaceholderMessage(sender, "wait")
        else {
            hubsPlayer.addTempStatus("rabbit", 60)
            sender.inventory.addItem(ItemStack(Material.RABBIT_FOOT))
            sendPlaceholderMessage(sender, "ok")
        }
        return true
    }

    override fun onHubsComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>) = null

}