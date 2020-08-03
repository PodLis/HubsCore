package su.hubs.hubscore

import org.bukkit.entity.Player
import su.hubs.hubscore.module.chesterton.internal.item.ChestertonItem
import su.hubs.hubscore.module.chesterton.internal.menu.ChestMenu

interface HubsServer {

    fun afterCoreStart(): Boolean

    fun beforeCoreStop()

    fun onPluginEnable()

    fun onPluginDisable()

    fun onPlayerJoin(player: Player?)

    fun onPlayerQuit(player: Player?)

    fun onReload()

    fun onStringsReload()

    fun onSchedule()

    fun getStringData(key: String?): String?

    fun getServerPermissions(): Array<HubsPermission>?

    fun getServerActions(): Map<
            String,
            (Player, ChestertonItem, ChestMenu, String) -> Unit
            >?

}