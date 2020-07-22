package ru.hubsmc.hubscore.module.essentials.abilities

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.StringUtil
import ru.hubsmc.hubscore.HubsCommand
import ru.hubsmc.hubscore.HubsPlayer
import ru.hubsmc.hubscore.Permissions
import ru.hubsmc.hubscore.PluginUtils
import java.util.ArrayList

class VisionCommand : HubsCommand("vision", Permissions.VISION, true,  0) {

    private fun visionOn(player: Player, hubsPlayer: HubsPlayer) {
        hubsPlayer.addStatus("vision")
        player.addPotionEffect(PotionEffect(PotionEffectType.NIGHT_VISION, 999999, 1, false, false))
        sendPlaceholderMessage(player, "turned_on")
    }

    private fun visionOff(player: Player, hubsPlayer: HubsPlayer) {
        hubsPlayer.removeStatus("vision")
        player.removePotionEffect(PotionEffectType.NIGHT_VISION)
        sendPlaceholderMessage(player, "turned_off")
    }

    override fun onHubsCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        val hubsPlayer = PluginUtils.getHubsPlayer(sender as Player)
        if (args.isEmpty()) {
            if (hubsPlayer.hasStatus("vision"))
                visionOff(sender, hubsPlayer)
            else
                visionOn(sender, hubsPlayer)
        } else {
            when (args[0]) {
                "on" -> visionOn(sender, hubsPlayer)
                "off" -> visionOff(sender, hubsPlayer)
                else -> sendDefaultUsage(sender)
            }
        }
        return true
    }

    override fun onHubsComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        val completionList: MutableCollection<String> = ArrayList()
        val partOfCommand: String
        val cmds = listOf("on", "off")
        if (args.size == 1) {
            partOfCommand = args[0]
            StringUtil.copyPartialMatches(partOfCommand, cmds, completionList)
            return completionList.sorted()
        }
        return null
    }
}