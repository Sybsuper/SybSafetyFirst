package com.sybsuper.sybsafetyfirst.modules

import org.bukkit.Bukkit
import org.bukkit.GameRule

class NoF3 : Module {
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