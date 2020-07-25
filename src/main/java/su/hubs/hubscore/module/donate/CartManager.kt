package su.hubs.hubscore.module.donate

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import su.hubs.hubscore.PluginUtils
import su.hubs.hubscore.module.chesterton.internal.ActionClickHandler
import su.hubs.hubscore.module.chesterton.internal.action.TakeFromCartItemAction
import su.hubs.hubscore.module.chesterton.internal.item.ExtendedItem
import su.hubs.hubscore.module.chesterton.internal.menu.ChestMenu
import su.hubs.hubscore.module.chesterton.internal.parser.ItemParser
import su.hubs.hubscore.module.chesterton.internal.parser.SubParser
import su.hubs.hubscore.module.values.PlayerData
import su.hubs.hubscore.util.MessageUtils
import su.hubs.hubscore.util.PlayerUtils

object CartManager {

    private val cartData: PlayerData = PlayerData("hubs_cart", "items")

    private var cycle: Byte = 0

    init {
        cartData.prepareToWork(arrayOf(""), intArrayOf(), doubleArrayOf())
    }

    fun addItemToCart(player: Player, item: String, amount: Int = 1) {
        val uuid = player.uniqueId.toString()
        if (!cartData.selectExist(uuid))
            cartData.createAccount(uuid)
        val map = stringToMap(cartData.selectStringValue(uuid, "items"))
        map.merge(item, amount, Int::plus)
        cartData.saveValue(uuid, "items", mapToString(map))
    }

    fun takeItemFromCart(player: Player, item: String): Boolean {
        val uuid = player.uniqueId.toString()
        if (PluginUtils.getHubsServer().getStringData("can_give_items")!!.toBoolean() && cartData.selectExist(uuid)) {
            val map = stringToMap(cartData.selectStringValue(uuid, "items"))
            if (map.containsKey(item)) {
                val section = PluginUtils.getConfigInCoreFolder("donate").getConfigurationSection(item)

                when (section?.getString("type")) {
                    "EXP_PACK" -> {
                        player.giveExp(section.getInt("exp"))
                    }
                    "LVL_PACK" -> {
                        player.giveExpLevels(section.getInt("exp"))
                    }
                    "ITEM_SET" -> {
                        for (sectionItem in section.getConfigurationSection("items")?.getKeys(false)!!) {
                            val subSection = section.getConfigurationSection("items.$sectionItem")
                            val itemStack = ItemStack(
                                    Material.getMaterial(subSection?.getString("type")?.toUpperCase()!!) ?: Material.BEDROCK,
                                    if (subSection.getInt("amount") > 1) subSection.getInt("amount") else 1
                            )
                            val itemMeta = itemStack.itemMeta!!
                            if (subSection.contains("name"))
                                itemMeta.setDisplayName(subSection.getString("name"))
                            if (subSection.contains("lore"))
                                itemMeta.lore = subSection.getStringList("lore")

                            itemMeta.isUnbreakable = subSection.getBoolean("unbreakable")
                            itemStack.itemMeta = itemMeta
                            if (subSection.contains("enchantments"))
                                itemStack.addUnsafeEnchantments(SubParser.parseEnchantments(subSection.getConfigurationSection("enchantments")))

                            player.inventory.addItem(itemStack)
                        }
                    }
                    else -> return false
                }
                map.merge(item, 0) { t, _ -> t - 1 }
                cartData.saveValue(uuid, "items", mapToString(map))
            }
            return false
        }
        MessageUtils.sendOnlyThatServerMessage(player, "survival")
        return false
    }

    fun openCart(player: Player) {
        val uuid = player.uniqueId.toString()
        val map: Map<String, Int>
        map = if (cartData.selectExist(uuid))
            stringToMap(cartData.selectStringValue(uuid, "items"))
        else mapOf()

        val menu = ChestMenu(PluginUtils.getStringsConfig().getString("menus.cart.title"), 6)
        var slot = 0
        for ((key, value) in map) {
            val section = PluginUtils.getConfigInCoreFolder("donate").getConfigurationSection(key)
            when (section?.getString("type")) {
                "EXP_PACK" -> {
                    menu.setItem(slot, ExtendedItem(Material.EXPERIENCE_BOTTLE).also {
                        it.setAmount(value)
                        it.setName("ОПЫТ: ${section.getInt("exp")}")
                        it.clickHandler = ActionClickHandler(TakeFromCartItemAction(key))
                    })
                    slot += 1
                }
                "LVL_PACK" -> {
                    menu.setItem(slot, ExtendedItem(Material.DRAGON_BREATH).also {
                        it.setAmount(value)
                        it.setName("УРОВНИ: ${section.getInt("exp")}")
                        it.clickHandler = ActionClickHandler(TakeFromCartItemAction(key))
                    })
                    slot += 1
                }
                "ITEM_SET" -> {
                    val subSection = section.getConfigurationSection("items.face")
                    menu.setItem(slot, ItemParser.parseExtendedItem(subSection).also {
                        it.setAmount(value)
                        it.setName(subSection?.getString("name"))
                        it.clickHandler = ActionClickHandler(TakeFromCartItemAction(key))
                    })
                    slot += 1
                }
            }
        }
        PlayerUtils.openMenuToPlayer(player, menu)
    }

    private fun stringToMap(string: String): HashMap<String, Int> {
        val map = hashMapOf<String, Int>()
        for (element in string.split(',')) {
            val keyAndValue = element.split(':')
            if (keyAndValue.size == 2)
                map[keyAndValue[0]] = keyAndValue[1].toInt()
        }
        return map
    }

    private fun mapToString(map: Map<String, Int>): String {
        val string = StringBuilder()
        for ((key, value) in map) {
            if (value > 0)
                string.append(key).append(":").append(value).append(",")
        }
        if (string.isNotEmpty())
            string.deleteCharAt(string.lastIndex)
        return string.toString()
    }

    fun makeCycle() {
        cartData.saveValue("cycle", "items", "$cycle")
        cycle++
    }

}