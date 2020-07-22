package ru.hubsmc.hubscore.module.essentials.info

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import ru.hubsmc.hubscore.HubsCommand
import ru.hubsmc.hubscore.PluginUtils
import ru.hubsmc.hubscore.module.loop.chat.ChatMessage
import ru.hubsmc.hubscore.util.StringUtils
import kotlin.collections.ArrayList

class ChangelogCommand : HubsCommand("changelog", null, true, 0) {

    private val changelog: FileConfiguration = PluginUtils.getConfigInCoreFolder("changelog")

    override fun onHubsCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.isEmpty()) {
            val curVer = changelog.getString("current_version")!!
            val message = ChatMessage(arrayOf(
                    "Текущая версия: '$curVer'",
                    "Дата релиза: ${changelog.getString("versions.$curVer.date")}",
                    "Изменения:"
            ) + StringUtils.replaceColor(changelog.getStringList("versions.$curVer.changes")))

            message.send(sender as Player)
        } else {
            val version: ConfigurationSection? = changelog.getConfigurationSection("versions.${args[0]}")
            if (version != null) {
                val message = ChatMessage(arrayOf(
                        "Информация о версии '${args[0]}'",
                        "Дата релиза: ${version.getString("date")}",
                        "Изменения:"
                ) + StringUtils.replaceColor(version.getStringList("changes")))

                message.send(sender as Player)
            }
        }
        return true
    }

    override fun onHubsComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        val completionList: MutableCollection<String> = ArrayList()
        val partOfCommand: String
        val cmds: List<String> = ArrayList(changelog.getConfigurationSection("versions")!!.getKeys(false))
        if (args.size == 1) {
            partOfCommand = args[0]
            StringUtil.copyPartialMatches(partOfCommand, cmds, completionList)
            val result: MutableList<String> = ArrayList()
            completionList.forEach { result.add(it) }
            return result
        }
        return null
    }
}