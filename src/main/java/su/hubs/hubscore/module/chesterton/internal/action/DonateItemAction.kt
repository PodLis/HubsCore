package su.hubs.hubscore.module.chesterton.internal.action

import org.bukkit.entity.Player
import su.hubs.hubscore.module.chesterton.internal.item.ChestertonItem
import su.hubs.hubscore.module.chesterton.internal.menu.ChestertonMenu
import su.hubs.hubscore.module.donate.HubsDonate.Companion.buyDonate

class DonateItemAction(private val donateKey: String, private val thisMenu: ChestertonMenu) : ItemAction() {

    override fun execute(player: Player, item: ChestertonItem) {
        buyDonate(player, donateKey, thisMenu, item)
    }

}