package com.sybsuper.sybsafetyfirst.modules

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

class NoSweepingDamage : Module {
    override val description: String = "Disables sweeping damage from swords."
    override var options: ModuleOptions = DefaultOptions()

    @EventHandler(ignoreCancelled = true)
    fun on(event: EntityDamageByEntityEvent) {
        if (event.cause != EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) return
        if (event.damager !is Player) return

        event.isCancelled = true
    }
}