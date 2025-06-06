package com.sybsuper.sybsafetyfirst.modules

import org.bukkit.Bukkit
import org.bukkit.GameRule

class LimitedCrafting : Module {
    override val name: String = "Limited Crafting"
    override val description: String =
        "Limits crafting recipes to only those that are available in the player's recipe book."
    override var options: ModuleOptions = DefaultOptions()

    override fun onEnable() {
        Bukkit.getWorlds().forEach {
            it.setGameRule(GameRule.DO_LIMITED_CRAFTING, true)
        }
    }

    override fun onDisable() {
        Bukkit.getWorlds().forEach {
            it.setGameRule(GameRule.DO_LIMITED_CRAFTING, false)
        }
    }
}