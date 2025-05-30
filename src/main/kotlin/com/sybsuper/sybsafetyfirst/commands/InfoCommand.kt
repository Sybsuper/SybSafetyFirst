package com.sybsuper.sybsafetyfirst.commands

import com.sybsuper.sybsafetyfirst.SybSafetyFirst
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.PluginDescriptionFile

object InfoCommand : SubCommand {
    override val name: String = "info"
    override val description: String = "Displays information about the plugin."

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        val description: PluginDescriptionFile? = SybSafetyFirst.Companion.instance.description
        sender.sendMessage(
            MiniMessage.miniMessage().deserialize("""
            <gold><bold>${description?.name}<br>
            <gray>${description?.description}<br>
            <gold>Version<dark_gray>: <white>${description?.version}<br>
            <gold>Author<dark_gray>: <white>${description?.authors?.joinToString(",")}
            ${if (!sender.hasPermission("sybsafetyfirst.admin")) "<br><gray>You do not have permission to run any of the sub-commands." else ""}
        """.trimIndent()))
        return true
    }
}