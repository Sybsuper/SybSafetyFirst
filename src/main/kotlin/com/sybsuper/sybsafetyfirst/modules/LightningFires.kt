package com.sybsuper.sybsafetyfirst.modules

import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import org.bukkit.block.data.type.Fire
import org.bukkit.entity.FallingBlock
import org.bukkit.event.EventHandler
import org.bukkit.event.weather.LightningStrikeEvent
import org.bukkit.util.Vector


class LightningFires : Module {
    override val description: String = "Makes lighting start fires."
    override var options: ModuleOptions = LightningFiresOptions()
    val typeSafeOptions: LightningFiresOptions
        get() = options as? LightningFiresOptions ?: error("options is not an instance of LightningFiresOptions")

    @Serializable
    data class LightningFiresOptions(
        override var enabled: Boolean = true,
        var amountOfFires: Int = 100,
        var velocity: Double = 0.66,
    ) : ModuleOptions

    @EventHandler(ignoreCancelled = true)
    fun on(event: LightningStrikeEvent) {
        val lightning = event.lightning
        val world = lightning.world
        var spawnloc = event.lightning.location.clone()
        while (world.getBlockAt(spawnloc).type.isSolid) {
            spawnloc = spawnloc.add(0.0, 1.0, 0.0) // Move up until we find a non-solid block
        }
        for (i in 0 until typeSafeOptions.amountOfFires) {
            val x = (Math.random() - 0.5) * 2
            val y = Math.random() * 2
            val z = (Math.random() - 0.5) * 2
            val fallingBlock = world.spawn(
                spawnloc, FallingBlock::class.java
            )
            val data = Bukkit.createBlockData(org.bukkit.Material.FIRE) as Fire
            fallingBlock.blockData = data
            val velocity = Vector(x, y, z).multiply(typeSafeOptions.velocity)
            fallingBlock.velocity = velocity
        }
    }
}