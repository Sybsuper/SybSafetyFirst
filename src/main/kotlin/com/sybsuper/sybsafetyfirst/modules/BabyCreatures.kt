package com.sybsuper.sybsafetyfirst.modules

import com.sybsuper.sybsafetyfirst.SybSafetyFirst
import kotlinx.serialization.Serializable
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Ageable
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.persistence.PersistentDataType
import kotlin.random.Random

class BabyCreatures : Module {
    override val description: String = "Allows baby variants of creatures to spawn."
    override var options: ModuleOptions = BabyCreaturesOptions()
    val typeSafeOptions
        get() = (options as? BabyCreaturesOptions) ?: error("Options are not of type BabyCreepersOptions")

    @Serializable
    data class BabyCreaturesOptions(
        override var enabled: Boolean = true,
        /**
         * Map of creature types to their options.
         * This map is automatically populated with all living entities that are not ageable.
         * Remove a type from this map to disable baby spawning for that creature.
         */
        var creatures: Map<EntityType, CreatureOptions> = EntityType.entries.filter { type ->
            type.entityClass?.let {
                LivingEntity::class.java.isAssignableFrom(it) && !Ageable::class.java.isAssignableFrom(it)
            } ?: false
        }.associateWith { CreatureOptions() },
    ) : ModuleOptions

    @Serializable
    data class CreatureOptions(
        /**
         * Chance for the creature to spawn as a baby.
         * Value should be between 0.0 and 1.0, where 1.0 means 100% chance.
         */
        var babyChance: Float = 0.1f,
        /**
         * Speed of the baby creature.
         * Default is 1.5, which makes baby creatures 50% faster than the normal speed.
         */
        var speedMultiplier: Double = 1.5,
        /**
         * Scale of the baby creature relative to the normal size.
         */
        var scale: Double = 0.5,
    )

    @EventHandler(ignoreCancelled = true)
    fun on(e: CreatureSpawnEvent) {
        val options = typeSafeOptions.creatures[e.entityType] ?: return
        if (Random.nextFloat() > options.babyChance) return
        val creature = e.entity
        val scale = creature.getAttribute(Attribute.SCALE) ?: error("Scale attribute not found for creeper")
        scale.baseValue = options.scale
        val speed =
            creature.getAttribute(Attribute.MOVEMENT_SPEED) ?: error("Movement speed attribute not found for creeper")
        speed.addModifier(
            AttributeModifier(
                NamespacedKey(
                    SybSafetyFirst.namespace, "baby_speed_modifier"
                ),
                options.speedMultiplier - 1,
                AttributeModifier.Operation.MULTIPLY_SCALAR_1
            )
        )
        creature.persistentDataContainer.set(
            NamespacedKey(SybSafetyFirst.namespace, "baby_creature"), PersistentDataType.BOOLEAN, true
        )
    }
}