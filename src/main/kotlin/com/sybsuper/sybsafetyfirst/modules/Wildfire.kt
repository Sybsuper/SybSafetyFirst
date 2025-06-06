package com.sybsuper.sybsafetyfirst.modules

import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.EntityType
import org.bukkit.entity.FallingBlock
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockSpreadEvent
import org.bukkit.util.Vector
import kotlin.random.Random

class Wildfire : Module {
    override val description: String = "Fire spreads like wildfire, consuming everything in its path. Be careful!"
    override var options: ModuleOptions = WildfireOptions()
    val typeSafeOptions
        get() = (options as? WildfireOptions) ?: error("Options are not of type WildfireOptions")

    @Serializable
    data class WildfireOptions(
        override var enabled: Boolean = true,
        /**
         * Chance for fire to spread to adjacent blocks.
         */
        var fireSpreadChance: Double = 0.5,
        /**
         * Velocity at which the fire jumps to nearby blocks.
         */
        var jumpVelocity: Double = 0.4,
        /**
         * Number of fire blocks that can be created from a single fire block.
         * Note: this is in addition to the original fire block that spreads.
         * For example, if this is set to 2, this means that a single fire spread leads to 3 fire blocks being added.
         * Also note: this scales the wildfire exponentially, so be careful with this value.
         */
        var amountOfFires: Int = 2,
    ) : ModuleOptions

    @EventHandler(ignoreCancelled = true)
    fun on(event: BlockSpreadEvent) {
        if (event.newState.type != Material.FIRE) return
        if (Random.nextFloat() > typeSafeOptions.fireSpreadChance) return
        spreadFire(event.block)
    }

    private fun spreadFire(block: Block) {
        val loc = block.location
        repeat(typeSafeOptions.amountOfFires) {
            val fire = loc.world.spawnEntity(loc, EntityType.FALLING_BLOCK) as FallingBlock
            fire.blockData = Bukkit.createBlockData(Material.FIRE)
            fire.velocity = Vector(
                Random.nextDouble(-1.0, 1.0),
                Random.nextDouble(0.5, 1.0),
                Random.nextDouble(-1.0, 1.0)
            ).multiply(typeSafeOptions.jumpVelocity)
        }
    }
}