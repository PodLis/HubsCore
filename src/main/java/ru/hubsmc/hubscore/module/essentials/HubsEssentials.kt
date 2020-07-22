package ru.hubsmc.hubscore.module.essentials

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.hubsmc.hubscore.CoreModule
import ru.hubsmc.hubscore.PluginUtils
import ru.hubsmc.hubscore.module.essentials.abilities.RabbitCommand
import ru.hubsmc.hubscore.module.essentials.abilities.VisionCommand
import ru.hubsmc.hubscore.module.essentials.info.*
import ru.hubsmc.hubscore.module.essentials.social.BroadhubsCommand
import ru.hubsmc.hubscore.module.loop.chat.RawMessage
import ru.hubsmc.hubscore.util.ConfigUtils
import ru.hubsmc.hubscore.util.JsonConverter
import ru.hubsmc.hubscore.util.StringUtils

class HubsEssentials : CoreModule() {

    override fun onEnable(): Boolean {
        loadFiles()
        PluginUtils.setCommandExecutorAndTabCompleter(
                RabbitCommand(),
                VisionCommand(),
                AboutCommand(),
                ChangelogCommand(),
                DiscordCommand(),
                FlexCommand(),
                FreeCommand(),
                HelpCommand(),
                KitsCommand(),
                VkCommand(),
                BroadhubsCommand()
        )
        return true
    }

    override fun onDisable() {}

    override fun onReload() {
        loadFiles()
    }

    override fun onPlayerJoin(player: Player) {}

    override fun onPlayerLeave(player: Player) {}

    override fun onSchedule(min: Byte) {}

    override fun onCommandExecute(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        return false
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        return null
    }

    private fun loadFiles() {
        val helpConfiguration = PluginUtils.getConfigInCoreFolder("help")
        JsonConverter.setHelpHoversExecutes(
                ConfigUtils.getStringsAndKeys(helpConfiguration.getConfigurationSection("hover"))[0],
                ConfigUtils.getStringsAndKeys(helpConfiguration.getConfigurationSection("hover"))[1],
                ConfigUtils.getStringsAndKeys(helpConfiguration.getConfigurationSection("execute"))[0],
                ConfigUtils.getStringsAndKeys(helpConfiguration.getConfigurationSection("execute"))[1]
        )
        for (key in helpConfiguration.getKeys(false)) {
            if (key == "hover" || key == "execute") continue
            if (key == "menu") {
                helpMessages[key] = RawMessage(StringUtils.listOfStringsToStringsArray(helpConfiguration.getStringList(key)), true, false)
            } else {
                for (subKey in helpConfiguration.getConfigurationSection(key)!!.getKeys(false)) {
                    if (subKey == "menu") {
                        helpMessages[key] = RawMessage(StringUtils.listOfStringsToStringsArray(helpConfiguration.getStringList("$key.$subKey")), true, false)
                    }
                    helpMessages["$key $subKey"] = RawMessage(StringUtils.listOfStringsToStringsArray(helpConfiguration.getStringList("$key.$subKey")), true, false)
                }
            }
        }

        val rawtextConfiguration = PluginUtils.getConfigInCoreFolder("rawtext")
        JsonConverter.setRawtextHoversExecutes(
                ConfigUtils.getStringsAndKeys(rawtextConfiguration.getConfigurationSection("hover"))[0],
                ConfigUtils.getStringsAndKeys(rawtextConfiguration.getConfigurationSection("hover"))[1],
                ConfigUtils.getStringsAndKeys(rawtextConfiguration.getConfigurationSection("execute"))[0],
                ConfigUtils.getStringsAndKeys(rawtextConfiguration.getConfigurationSection("execute"))[1]
        )
        for (key in rawtextConfiguration.getKeys(false)) {
            if (key == "hover" || key == "execute") continue
            rawtextMessages[key] = RawMessage(StringUtils.listOfStringsToStringsArray(rawtextConfiguration.getStringList(key)), false, true)
        }
    }

    companion object {
        private val helpMessages = hashMapOf<String, RawMessage>()
        private val rawtextMessages = hashMapOf<String, RawMessage>()

        fun sendHelpMessage(key: String, player: Player?) {
            if (helpMessages.containsKey(key)) helpMessages[key]?.send(player) else helpMessages["menu"]?.send(player)
        }

        fun getHelpMessageNames(): Set<String> {
            return helpMessages.keys
        }

        fun sendRawtextMessage(key: String, player: Player?) {
            if (rawtextMessages.containsKey(key)) rawtextMessages[key]?.send(player)
        }

        fun getRawtextMessageNames(): Set<String> {
            return rawtextMessages.keys
        }
    }

}