package su.hubs.hubscore.module.chesterton.internal.parser

import org.bukkit.Color
import org.bukkit.NamespacedKey
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.enchantments.Enchantment
import org.bukkit.potion.PotionData
import org.bukkit.potion.PotionType
import su.hubs.hubscore.PluginUtils
import su.hubs.hubscore.module.chesterton.internal.action.*
import su.hubs.hubscore.module.chesterton.internal.menu.ChestMenu

object SubParser {

    fun parsePotionData(section: ConfigurationSection): PotionData {
        return PotionData(PotionType.valueOf(section.getString("effect")!!), section.getBoolean("extended"), section.getBoolean("upgraded"))
    }

    fun parseEnchantments(section: ConfigurationSection): Map<Enchantment?, Int> {
        val enchantments: MutableMap<Enchantment?, Int> = HashMap()
        for (enchantment in section.getKeys(false)) {
            enchantments[Enchantment.getByKey(NamespacedKey.minecraft(enchantment!!))] = section.getInt(enchantment)
        }
        return enchantments
    }

    fun parseColor(string: String): Color {
        val strings = string.split(":")
        return when (strings.size) {
            1 -> Color.fromRGB(strings[0].toInt())
            3 -> Color.fromRGB(strings[0].toInt(), strings[1].toInt(), strings[2].toInt())
            else -> Color.BLACK
        }
    }

    fun parseAction(string: String, menu: ChestMenu?): ItemAction {
        val strings = string.split(":")
        if (strings.size > 1)
            when (strings[0]) {
                "open" -> return OpenMenuItemAction(menu, strings[1])
                "open_beside" -> return OpenMenuBesideItemAction(menu, strings[1])
                "command" -> return CommandExecuteAction(strings[1])
                "server" -> return ServerChangeItemAction(strings[1])
                "buy" -> return DonateItemAction(strings[1], menu!!)
                else -> {
                    val actions = PluginUtils.getHubsServer().getServerActions()
                    if (actions == null)
                        return ReturnItemAction(menu)
                    else {
                        for ((key, value) in actions) {
                            if (strings[0] == key) {
                                return CustomItemAction(menu!!, strings[1], value)
                            }
                        }
                    }
                    return ReturnItemAction(menu)
                }
            }
        return ReturnItemAction(menu)
    }
}
