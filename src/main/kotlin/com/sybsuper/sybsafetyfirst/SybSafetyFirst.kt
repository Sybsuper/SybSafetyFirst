package com.sybsuper.sybsafetyfirst

import com.sybsuper.bstats.Metrics
import com.sybsuper.sybsafetyfirst.commands.MainCommand
import com.sybsuper.sybsafetyfirst.modules.ModuleManager
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class SybSafetyFirst : JavaPlugin() {
    override fun onEnable() {
        instance = this
        namespace = this.name.lowercase()
        Bukkit.getPluginCommand("sybsafetyfirst")?.run {
            setExecutor(MainCommand)
            tabCompleter = MainCommand
        }
        // initialize the module manager
        ModuleManager
        // enable bStats metrics
        Metrics(this, 26582)
    }

    override fun onDisable() {
    }

    companion object {
        /**
         * The singleton instance of the plugin.
         * This is initialized in the onEnable method.
         */
        lateinit var instance: SybSafetyFirst
            private set

        /**
         * The namespace for the plugin, used for configuration and data storage.
         * This is the plugin's name in lowercase.
         */
        lateinit var namespace: String
            private set
    }
}
