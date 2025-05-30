package com.sybsuper.sybsafetyfirst.commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

object MainCommand : CommandExecutor, TabCompleter {
    val subcommands =
        listOf<SubCommand>(InfoCommand, HelpCommand, EnableCommand, DisableCommand, ReloadCommand, ModulesCommand)

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (!sender.hasPermission("sybsafetyfirst.admin")) {
            return InfoCommand.onCommand(sender, command, label, args)
        }

        if (args.isEmpty()) {
            return HelpCommand.onCommand(sender, command, label, args)
        }

        val subcommandName = args[0].lowercase()
        val subcommand = subcommands.find { it.name.equals(subcommandName, ignoreCase = true) }

        return if (subcommand != null) {
            val result = subcommand.onCommand(sender, command, label, args.drop(1).toTypedArray())
            if (!result) {
                sender.sendMessage("Usage: ${subcommand.usage}")
            }
            true
        } else {
            sender.sendMessage(
                Component.text("Unknown subcommand: $subcommandName ")
                    .append(
                        Component.text("(Click here for help)")
                            .clickEvent(ClickEvent.runCommand("/sybsafetyfirst help"))
                    )
            )
            false
        }
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): List<String?>? {
        if (!sender.hasPermission("sybsafetyfirst.admin")) return null
        val allowedCommands = subcommands.filter {
            it.permission?.let { perm -> sender.hasPermission(perm) } ?: true
        }
        return if (args.isEmpty()) {
            allowedCommands.map { it.name.lowercase() }
        } else {
            val lastArg = args.lastOrNull()?.lowercase().orEmpty()
            allowedCommands.filter { it.name.lowercase().startsWith(lastArg) }
                .map { it.name }
        }
    }
}