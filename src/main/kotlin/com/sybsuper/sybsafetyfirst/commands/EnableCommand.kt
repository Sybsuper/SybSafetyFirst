package com.sybsuper.sybsafetyfirst.commands

import com.sybsuper.sybsafetyfirst.modules.ModuleManager
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

object EnableCommand : SubCommand {
    override val name: String = "enable"
    override val description: String = "Enables a module."
    override val usage: String = super.usage + " <module>"

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
        ModuleManager.enableModule(module).onFailure {
            sender.sendMessage("Failed to enable module '${module.name}': ${it.message}")
            return false
        }
        sender.sendMessage("Module '${module.name}' has been enabled.")
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): List<String?>? {
        return if (args.size == 1) {
            val needle = args[0].lowercase()
            ModuleManager.disabledModuleIds.filter { it.startsWith(needle) }
        } else {
            null
        }
    }
}
