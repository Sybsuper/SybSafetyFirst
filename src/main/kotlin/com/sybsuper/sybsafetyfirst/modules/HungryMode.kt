package com.sybsuper.sybsafetyfirst.modules

import com.sybsuper.sybsafetyfirst.utils.delay
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerItemConsumeEvent

object HungryMode : Module {
    override val name: String = "Hungry Mode"
    override val description: String = "Makes players more hungry."
    override var options: ModuleOptions = DefaultOptions()

    @EventHandler
    fun on(event: PlayerItemConsumeEvent) {
        delay(1) {
            if (event.isCancelled) return@delay
            event.player.saturation = 0f
        }
    }
}