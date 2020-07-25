package su.hubs.hubscore.module.donate

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import su.hubs.hubscore.CoreModule
import su.hubs.hubscore.PluginUtils
import su.hubs.hubscore.module.chesterton.internal.item.ChestertonItem
import su.hubs.hubscore.module.chesterton.internal.menu.ChestertonMenu
import su.hubs.hubscore.module.chesterton.internal.menu.ConfirmMenu
import su.hubs.hubscore.module.donate.commands.CartCommand
import su.hubs.hubscore.module.donate.commands.DonateCommand
import su.hubs.hubscore.module.essentials.HubsEssentials
import su.hubs.hubscore.module.values.ValueType
import su.hubs.hubscore.module.values.api.API.addDollars
import su.hubs.hubscore.module.values.api.API.takeHubixes
import su.hubs.hubscore.util.MessageUtils
import su.hubs.hubscore.util.PlayerUtils

class HubsDonate : CoreModule() {

    override fun onEnable(): Boolean {
        PluginUtils.setCommandExecutorAndTabCompleter(DonateCommand(), CartCommand())
        return true
    }

    override fun onDisable() {}

    override fun onReload() {}

    override fun onPlayerJoin(player: Player) {}

    override fun onPlayerLeave(player: Player) {}

    override fun onSchedule(min: Byte) {
        CartManager.makeCycle()
    }

    override fun onCommandExecute(sender: CommandSender, command: Command, label: String, args: Array<String>) = false

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>) = null

    companion object {
        fun buyDonate(player: Player, donateKey: String, prevMenu: ChestertonMenu, prevItem: ChestertonItem) {
            val section = PluginUtils.getConfigInCoreFolder("donate").getConfigurationSection(donateKey)
            val price = section?.getInt("price") ?: 999
            val menu = ConfirmMenu(
                    prevItem.material,
                    prevItem.createItemStack(player).itemMeta?.displayName,
                    ValueType.HUBIXES,
                    price
            ) { _, _ ->
                if (!takeHubixes(player, price)) {
                    MessageUtils.sendNotEnoughHubixesMessage(player)
                } else {
                    when (section?.getString("type")) {
                        "TEMP_PERM" -> {
                        }
                        "TEMP_GROUP" -> {
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                                    "lp user ${player.name} parent removetemp ${section.getString("group")}"
                            )
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                                    "lp user ${player.name} parent addtemp ${section.getString("group")} ${section.getString("long")}"
                            )
                        }
                        "DOLLAR_PACK" -> {
                            addDollars(player, section.getInt("dollars"))
                        }
                        "EXP_PACK", "LVL_PACK", "ITEM_SET" -> {
                            CartManager.addItemToCart(player, donateKey)
                        }
                    }
                    HubsEssentials.sendRawtextMessage("donate_$donateKey", player)
                }
                true
            }
            menu.parentMenu = prevMenu
            PlayerUtils.openMenuToPlayer(player, menu)
        }
    }
}