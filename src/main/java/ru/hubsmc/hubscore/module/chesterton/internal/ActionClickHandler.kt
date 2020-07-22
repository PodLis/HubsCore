package ru.hubsmc.hubscore.module.chesterton.internal

import org.bukkit.entity.Player
import ru.hubsmc.hubscore.module.chesterton.internal.action.ItemAction

class ActionClickHandler(private val action: ItemAction?, private val close: Boolean) : ClickHandler {
    constructor(action: ItemAction?) : this(action, false)

    override fun onClick(player: Player): Boolean {
        action?.execute(player)
        return close
    }

}