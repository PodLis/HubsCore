package su.hubs.hubscore.module.chesterton.internal.action

import org.bukkit.entity.Player
import su.hubs.hubscore.PluginUtils
import su.hubs.hubscore.module.chesterton.internal.item.ChestertonItem
import su.hubs.hubscore.module.chesterton.internal.menu.ChestMenu

class CustomItemAction(
        private val thisMenu: ChestMenu,
        private val data: String,
        private val action: (Player, ChestertonItem, ChestMenu, String) -> Unit
) : ItemAction() {

    override fun execute(player: Player, item: ChestertonItem) {
        PluginUtils.logConsole("CustomItemAction: execute")
        action(player, item, thisMenu, data)
    }

}
