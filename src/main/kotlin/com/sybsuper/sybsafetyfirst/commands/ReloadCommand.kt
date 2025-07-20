package com.sybsuper.sybsafetyfirst.commands

import com.sybsuper.sybsafetyfirst.modules.ModuleManager
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

object ReloadCommand : SubCommand {
    override val name: String = "reload"
    override val description: String = "Reloads a module."
    override val usage: String = super.usage + " <module>"

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        val moduleId = args.getOrNull(0)?.lowercase() ?: return false
        var module = ModuleManager.fromId(moduleId) ?: run {
            sender.sendMessage("Module '$moduleId' not found.")
            return false
        }
        module = module.currentEnabledInstance() ?: module
        ModuleManager.reloadModule(module).onFailure {
            sender.sendMessage("Failed to reload module '${module.name}': ${it.message}")
            it.printStackTrace()
            return false
        }
        sender.sendMessage("Module '${module.name}' has been reloaded.")
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
            ModuleManager.modules.map { it.id }.filter { it.startsWith(needle) }
        } else {
            null
        }
    }
}
