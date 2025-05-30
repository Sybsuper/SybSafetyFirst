package com.sybsuper.sybsafetyfirst.modules

import org.bukkit.Bukkit
import org.bukkit.GameRule

object NoF3 : Module {
    override val name: String = "No F3"
    override val description: String = "Disables the F3 debug screen."
    override var options: ModuleOptions = DefaultOptions()

    override fun onEnable() {
        Bukkit.getWorlds().forEach { world ->
            world.setGameRule(GameRule.REDUCED_DEBUG_INFO, true)
        }
    }

    override fun onDisable() {
        Bukkit.getWorlds().forEach { world ->
            world.setGameRule(GameRule.REDUCED_DEBUG_INFO, false)
        }
    }
}