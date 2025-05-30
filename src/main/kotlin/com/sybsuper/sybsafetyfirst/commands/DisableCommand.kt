package com.sybsuper.sybsafetyfirst.commands

import com.sybsuper.sybsafetyfirst.modules.ModuleManager
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

object DisableCommand : SubCommand {
    override val name: String = "disable"

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        val moduleId = args.getOrNull(0)?.lowercase() ?: return false
        val module = ModuleManager.fromId(moduleId) ?: run {
            sender.sendMessage("Module '$moduleId' not found.")
            return false
        }
        ModuleManager.disableModule(module)
        sender.sendMessage("Module '${module.name}' has been disabled.")
        return true
    }
}
