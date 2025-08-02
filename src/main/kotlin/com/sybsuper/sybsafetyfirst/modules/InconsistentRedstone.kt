package com.sybsuper.sybsafetyfirst.modules

import kotlinx.serialization.Serializable
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockRedstoneEvent
import kotlin.random.Random

class InconsistentRedstone : Module {
    override val description: String = "Redstone components might not work as expected."
    override var options: ModuleOptions = InconsistentRedstoneOptions()
    val typeSafeOptions
        get() = (options as? InconsistentRedstoneOptions)
            ?: error("Options are not of type InconsistentRedstoneOptions")

    @Serializable
    data class InconsistentRedstoneOptions(
        override var enabled: Boolean = true,
        /**
         * The chance of a redstone component being inconsistent.
         */
        var inconsistencyChance: Map<Material, Float> = mapOf(
            Material.REDSTONE to 0.05f,
            Material.REDSTONE_TORCH to 0.05f,
            Material.REDSTONE_WALL_TORCH to 0.05f,
            Material.REDSTONE_WIRE to 0.05f,
            Material.REPEATER to 0.05f,
            Material.COMPARATOR to 0.05f,
        ),
    ) : ModuleOptions

    @EventHandler(ignoreCancelled = true)
    fun onRedstoneEvent(event: BlockRedstoneEvent) {
        val material = event.block.type
        typeSafeOptions.inconsistencyChance[material]?.let { chance ->
            if (Random.nextFloat() < chance) {
                event.newCurrent = event.oldCurrent
            }
        }
    }
}