package com.sybsuper.sybsafetyfirst.modules

import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.entity.Monster
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityTargetEvent
import org.bukkit.event.entity.EntityTargetLivingEntityEvent
import kotlin.random.Random

object HostileReinforcements : Module {
    override val name: String = "Hostile Reinforcements"
    override val description: String =
        "Hostile mobs call for reinforcements when it hits a player or is hit by a player."
    override var options: ModuleOptions = HostileReinforcementsOptions()
    val typeSafeOptions
        get() = (options as? HostileReinforcementsOptions)
            ?: error("Options are not of type HostileReinforcementsOptions")

    @Serializable
    data class HostileReinforcementsOptions(
        override val enabled: Boolean = true,
        /**
         * The number of reinforcements to redirect towards the player.
         */
        val reinforcementCount: Int = 3,
        /**
         * Chunks around the entity where reinforcements are called.
         */
        val spawnChunkRadius: Int = 3
    ) : ModuleOptions

    @EventHandler(ignoreCancelled = true)
    fun onEntityHit(event: EntityDamageByEntityEvent) {
        val entity = event.entity.let { if (it is Projectile) it.shooter else it }
        val damager = event.damager.let { if (it is Projectile) it.shooter else it }
        // only apply if the entity is a hostile mob and the damager is a player or the other way around
        val (player, monster) = when {
            entity is Monster && damager is Player -> damager to entity
            damager is Monster && entity is Player -> entity to damager
            else -> return
        }

        callReinforcements(monster, player)
    }

    private fun callReinforcements(monster: Monster, player: Player) {
        var found = 0
        loopChunks(monster.location, typeSafeOptions.spawnChunkRadius) { chunk ->
            chunk.entities.forEach { entity ->
                if (entity !is Monster) return@forEach
                if (found >= typeSafeOptions.reinforcementCount) return
                if (entity.target == null) {
                    val event = EntityTargetLivingEntityEvent(
                        entity,
                        player,
                        EntityTargetEvent.TargetReason.REINFORCEMENT_TARGET
                    )
                    Bukkit.getPluginManager().callEvent(event)
                    if (event.isCancelled) return@forEach
                    entity.target = player
                    found++
                }
            }
        }
    }

    private inline fun loopChunks(location: Location, spawnChunkRadius: Int, function: (Chunk) -> Unit) {
        // loop over the chunks around the location in the closest first order
        val world = location.world ?: return
        val centerX = location.chunk.x
        val centerZ = location.chunk.z
        for (x in (0..spawnChunkRadius).flatMap { listOf(it, -it) }.distinct()) {
            for (z in (0..spawnChunkRadius).flatMap { listOf(it, -it) }.distinct()) {
                val chunkX = centerX + x
                val chunkZ = centerZ + z
                val chunk = world.getChunkAt(chunkX, chunkZ)
                if (!chunk.isLoaded) continue
                function(chunk)
            }
        }
    }

    private fun getRandomLocationAround(location: Location, radius: Double): Location {
        val randomX = location.x + Random.nextDouble(-radius, radius)
        val randomZ = location.z + Random.nextDouble(-radius, radius)
        val randomY = location.world.getHighestBlockYAt(randomX.toInt(), randomZ.toInt()).toDouble()
        return Location(location.world, randomX, randomY, randomZ)
    }
}