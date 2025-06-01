package com.sybsuper.sybsafetyfirst.modules

import kotlinx.serialization.Serializable
import org.bukkit.attribute.Attribute
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent

object HeavyArmor : Module {
    override val name: String = "Heavy Armor"
    override val description: String = "The heavier the armor, the slower you move. "
    override var options: ModuleOptions = HeavyArmorOptions()
    val typeSafeOptions
        get() = (options as? HeavyArmorOptions) ?: error("Options are not of type HeavyArmorOptions")

    /**
     * Note: Default values are set to set the player movement speed to 50% of the default speed when the player is wearing full netherite armor.
     */
    @Serializable
    data class HeavyArmorOptions(
        override val enabled: Boolean = true,
        /**
         * The base speed of the player when not wearing any armor.
         * Minecraft default Player speed is 0.2.
         * This means that with no armor, the player will move at 0.2 blocks per tick.
         */
        val baseSpeed: Double = 0.2,
        /**
         * The speed decrease per armor defense point. See: https://minecraft.fandom.com/wiki/Armor#Defense_points
         * Max multiplier is 20 points (full diamond or netherite armor).
         * For context:
         * Minecraft default Player speed is 0.2
         * This means that with full diamond armor, the player will move at 0.2 - (20 * [speedDecreasePerDefensePoint]) blocks per tick.
         */
        val speedDecreasePerDefensePoint: Double = 0.00125,
        /**
         * The speed decrease per toughness point. See: https://minecraft.fandom.com/wiki/Armor#Armor_toughness
         * Max multiplier is 12 points (full netherite armor).
         * For context:
         * Minecraft default Player speed is 0.2
         * This means that with full netherite armor, the player will move at 0.2 - (12 * [speedDecreasePerToughnessPoint]) blocks per tick.
         */
        val speedDecreasePerToughnessPoint: Double = 0.00625,
        ) : ModuleOptions

    @EventHandler(ignoreCancelled = true)
    fun on(event: PlayerMoveEvent) {
        val player = event.player
        val defensePoints = player.getAttribute(Attribute.ARMOR)?.value ?: 0.0
        val toughnessPoints = player.getAttribute(Attribute.ARMOR_TOUGHNESS)?.value ?: 0.0
        val speedDecrease = (defensePoints * typeSafeOptions.speedDecreasePerDefensePoint) +
                (toughnessPoints * typeSafeOptions.speedDecreasePerToughnessPoint)
        val newSpeed = typeSafeOptions.baseSpeed - speedDecrease
        player.walkSpeed = newSpeed.toFloat()
    }
}