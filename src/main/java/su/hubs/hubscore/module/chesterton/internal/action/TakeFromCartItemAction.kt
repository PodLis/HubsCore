package su.hubs.hubscore.module.chesterton.internal.action

import org.bukkit.entity.Player
import su.hubs.hubscore.module.chesterton.internal.item.ChestertonItem
import su.hubs.hubscore.module.donate.CartManager.openCart
import su.hubs.hubscore.module.donate.CartManager.takeItemFromCart

class TakeFromCartItemAction(private val item: String) : ItemAction() {

    override fun execute(player: Player, chestertonItem: ChestertonItem) {
        takeItemFromCart(player, item)
        openCart(player)
    }

}