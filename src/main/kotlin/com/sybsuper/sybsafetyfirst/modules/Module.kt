package com.sybsuper.sybsafetyfirst.modules

import com.sybsuper.sybsafetyfirst.utils.fromPascalCase
import com.sybsuper.sybsafetyfirst.utils.toSnakeCase
import org.bukkit.event.Listener

interface Module : Listener {
    val id: String
        get() = javaClass.simpleName.fromPascalCase().toSnakeCase()
    val name: String
        get() = javaClass.simpleName.fromPascalCase()
            .joinToString(" ") { it.replaceFirstChar { c -> c.uppercaseChar() } }
    val description: String
    var options: ModuleOptions
    fun currentEnabledInstance(): Module? = ModuleManager.enabledInstanceFromId(this.id)

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