package com.sybsuper.sybsafetyfirst.modules

import kotlinx.serialization.Serializable
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.potion.PotionEffect

class BrokenBones : Module {
    override val description: String =
        "Simulates broken bones when a player falls from a height. Temporarily slowing them."
    override var options: ModuleOptions = BrokenBonesOptions()
    val typeSafeOptions
        get() = (options as? BrokenBonesOptions) ?: error("Options are not of type BrokenBonesOptions")

    @Serializable
    data class BrokenBonesOptions(
        override var enabled: Boolean = true,
        /**
         * The duration in ticks for which the player is slowed after breaking bones.
         * Default is 5 seconds.
         */
        var slowDurationTicks: Int = 100,
        /**
         * The level of slowness to apply.
         * Default is 1, which corresponds to Slowness I.
         */
        var slowLevel: Int = 1
    ) : ModuleOptions

    @EventHandler(ignoreCancelled = true)
    fun on(event: EntityDamageEvent) {
        if (event.entity !is Player || event.cause != EntityDamageEvent.DamageCause.FALL) return
        val player = event.entity as Player

        player.addPotionEffect(
            PotionEffect(
                org.bukkit.potion.PotionEffectType.SLOWNESS,
                typeSafeOptions.slowDurationTicks,
                // bukkit slowness levels are 0-indexed, so we subtract 1
                (typeSafeOptions.slowLevel - 1).coerceAtLeast(0),
                false,
                false,
                false
            )
        )
    }
}