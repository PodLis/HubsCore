package ru.hubsmc.hubscore.module.values.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.hubsmc.hubscore.HubsCommand
import ru.hubsmc.hubscore.Permissions
import ru.hubsmc.hubscore.module.values.api.API.addDollars

class BonusCommand : HubsCommand("bonus", null, true, 0) {

    override fun onHubsCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        val player = sender as Player
        if (!Permissions.BONUS.senderHasPerm(player)) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "lp user ${player.uniqueId} permission settemp hubs.bonus true 22h")
            addDollars(player, 500)
            sendPlaceholderMessage(player, "ok")
        } else {
            sendPlaceholderMessage(player, "wait")
        }
        return true
    }

    override fun onHubsComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>) = null

}