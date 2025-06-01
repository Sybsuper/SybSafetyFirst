package com.sybsuper.sybsafetyfirst.modules

import kotlinx.serialization.Serializable
import org.bukkit.entity.Ageable
import org.bukkit.entity.Monster
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

object PlayerZombies : Module {
    override val name: String = "Player Zombies"
    override val description: String = "When a player dies, they will turn into a zombie."
    override var options: ModuleOptions = PlayerZombiesOptions()
    val typeSafeOptions
        get() = (options as? PlayerZombiesOptions) ?: error("Options are not of type PlayerZombiesOptions")

    @Serializable
    data class PlayerZombiesOptions(
        override val enabled: Boolean = true,
        /**
         * Whether the zombie will be a zombie villager (true) or a regular zombie (false).
         */
        val zombieVillager: Boolean = true,
        /**
         * The chance that the zombie will be a baby zombie.
         * Value should be between 0.0 and 1.0, where 1.0 means 100% chance.
         */
        val babyChance: Float = 0.1f,
        /**
         * Drop chance for the zombie's equipment. Chance for each piece of equipment to be dropped.
         * When set to 50% on average, half of the equipment will be dropped.
         * Value should be between 0.0 and 1.0, where 1.0 means 100% chance.
         */
        val dropEquipmentChance: Float = 0.66f,
    ) : ModuleOptions

    @EventHandler(ignoreCancelled = true)
    fun on(event: PlayerDeathEvent) {
        val player = event.entity
        val type = if (typeSafeOptions.zombieVillager) {
            org.bukkit.entity.EntityType.ZOMBIE_VILLAGER
        } else {
            org.bukkit.entity.EntityType.ZOMBIE
        }
        val zombie = player.world.spawnEntity(player.location, type) as Monster
        if (Random.nextFloat() < typeSafeOptions.babyChance) {
            (zombie as? Ageable)?.setBaby()
        }
        fun moveSlot(slot: EquipmentSlot) {
            val item: ItemStack? = player.inventory.getItem(slot)
            @Suppress("KotlinConstantConditions")
            if (item != null) {
                event.drops.remove(item)
                player.inventory.setItem(slot, null)
                zombie.equipment.setItem(slot, item)
                zombie.equipment.setDropChance(slot, typeSafeOptions.dropEquipmentChance)
            }
        }
        listOf(
            EquipmentSlot.HAND,
            EquipmentSlot.OFF_HAND,
            EquipmentSlot.FEET,
            EquipmentSlot.LEGS,
            EquipmentSlot.CHEST,
            EquipmentSlot.HEAD
        ).forEach { moveSlot(it) }
        zombie.customName(event.player.displayName())
        zombie.isCustomNameVisible = true
        zombie.arrowsInBody = event.player.arrowsInBody
        zombie.canPickupItems = true
        zombie.isPersistent = true
    }
}