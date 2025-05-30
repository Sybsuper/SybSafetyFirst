package com.sybsuper.sybsafetyfirst.commands

import com.sybsuper.sybsafetyfirst.modules.ModuleManager
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

object ModulesCommand : SubCommand {
    override val name: String = "modules"

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        sender.sendMessage("Available modules:")
        ModuleManager.modules.forEach {
            if (ModuleManager.isEnabled(it)) {
                sender.sendMessage("✔ ${it.name} (Enabled)")
            } else {
                sender.sendMessage("✘ ${it.name} (Disabled)")
            }
        }
        return true
    }

}
