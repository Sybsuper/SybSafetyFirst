package com.sybsuper.sybsafetyfirst.commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

object HelpCommand : SubCommand {
    override val name: String = "help"
    override val description: String = "Displays a list of available commands."

    override fun onCommand(
        sender: CommandSender, command: Command, label: String, args: Array<out String>
    ): Boolean {
        if (!sender.hasPermission("sybsafetyfirst.admin")) {
            sender.sendMessage("You do not have permission to use this command.")
            return false
        }
        sender.sendMessage("Available commands:")
        MainCommand.subcommands.forEach { subcommand ->
            if (!sender.hasPermission(subcommand.permission)) return@forEach
            sender.sendMessage(
                Component.text(subcommand.name).append(
                    Component.text(": ")
                ).append(
                    Component.text(subcommand.description)
                ).hoverEvent(
                    HoverEvent.showText(
                        Component.text("Usage: ${subcommand.usage}")
                    )
                )
            )
        }
        return true
    }
}