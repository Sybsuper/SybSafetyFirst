package com.sybsuper.sybsafetyfirst.modules

import org.bukkit.event.Listener

interface Module : Listener {
    val id: String
        get() = javaClass.simpleName.lowercase()
    val name: String
    val description: String
    var options: ModuleOptions

    /**
     * Called when the module is enabled.
     */
    fun onEnable() {
    }

    /**
     * Called when the module is disabled.
     */
    fun onDisable() {
    }
}