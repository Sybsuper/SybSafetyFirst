package com.sybsuper.sybsafetyfirst.modules

import kotlinx.serialization.Serializable
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Creeper
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.CreatureSpawnEvent
import kotlin.random.Random

class BabyCreepers : Module {
    override val description: String = "Adds a baby variant of creepers, similar to baby zombies."
    override var options: ModuleOptions = BabyCreepersOptions()
    val typeSafeOptions
        get() = (options as? BabyCreepersOptions) ?: error("Options are not of type BabyCreepersOptions")

    @Serializable
    data class BabyCreepersOptions(
        override var enabled: Boolean = true,
        /**
         * The chance of a creeper spawning as a baby creeper.
         * Value should be between 0.0 and 1.0, where 1.0 means every creeper is a baby.
         */
        var babyCreeperChance: Float = 0.05f,
        /**
         * The scale of the baby creeper.
         * Default is 0.5, which is half the size of a regular creeper.
         */
        var scale: Double = 0.5,
        /**
         * Speed at which the baby creeper moves.
         * Minecraft default is 0.25
         */
        var speed: Double = 0.5,
    ) : ModuleOptions

    @EventHandler(ignoreCancelled = true)
    fun on(e: CreatureSpawnEvent) {
        if (e.entityType != EntityType.CREEPER) return
        if (Random.nextFloat() > typeSafeOptions.babyCreeperChance) return
        val creeper = e.entity
        if (creeper !is Creeper) return
        val scale = creeper.getAttribute(Attribute.SCALE) ?: error("Scale attribute not found for creeper")
        scale.baseValue = 0.5
        val speed = creeper.getAttribute(Attribute.MOVEMENT_SPEED)
            ?: error("Movement speed attribute not found for creeper")
        speed.baseValue = typeSafeOptions.speed
    }
}