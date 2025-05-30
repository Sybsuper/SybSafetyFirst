package com.sybsuper.sybsafetyfirst

import com.sybsuper.sybsafetyfirst.commands.MainCommand
import com.sybsuper.sybsafetyfirst.modules.ModuleManager
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class SybSafetyFirst : JavaPlugin() {

    override fun onEnable() {
        instance = this
        val pluginCommand = Bukkit.getPluginCommand("sybsafetyfirst")?.run {
            setExecutor(MainCommand)
            tabCompleter = MainCommand
        }
        // initialize the module manager
        ModuleManager
    }

    override fun onDisable() {
    }

    companion object {
        lateinit var instance: SybSafetyFirst
    }
}
