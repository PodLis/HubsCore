package su.hubs.hubscore.module.chesterton.internal

import org.bukkit.entity.Player
import su.hubs.hubscore.module.chesterton.internal.action.ItemAction
import su.hubs.hubscore.module.chesterton.internal.item.ChestertonItem

class ActionClickHandler(private val action: ItemAction?, private val close: Boolean) : ClickHandler {
    constructor(action: ItemAction?) : this(action, false)

    override fun onClick(player: Player, item: ChestertonItem): Boolean {
        action?.execute(player, item)
        return close
    }

}