package com.sybsuper.sybsafetyfirst.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

interface SubCommand : CommandExecutor, TabCompleter {
    val name: String
    val permission: String
        get() = "sybsafetyfirst.admin.$name"

    val description: String
        get() = "No description provided."

    val usage: String
        get() = "/sybsafetyfirst ${this::class.simpleName?.lowercase() ?: "unknown"}"

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): List<String?>? = null
}