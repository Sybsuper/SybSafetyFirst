package com.sybsuper.sybsafetyfirst.modules

import com.sybsuper.sybsafetyfirst.utils.delay
import kotlinx.serialization.Serializable
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*

class HungerDelirium : Module {
    override val description: String = "When a player is starving, they will experience nausea."
    override var options: ModuleOptions = HungerDeliriumOptions()
    private val typeSafeOptions
        get() = (options as? HungerDeliriumOptions) ?: error("Options are not of type HungerDeliriumOptions")
    val isBeingWatched = mutableSetOf<UUID>()

    @Serializable
    data class HungerDeliriumOptions(
        override var enabled: Boolean = true,
        /**
         * The minimum food level below (less or equal to) which the player will start experiencing nausea.
         * Minecraft max food level is 20, so this value should be between 0 and 20.
         */
        var minFoodLevel: Int = 7,
    ) : ModuleOptions

    @EventHandler(ignoreCancelled = true)
    fun on(event: FoodLevelChangeEvent) {
        val player = event.entity
        if (player !is Player) return
        if (!isBeingWatched.add(player.uniqueId)) return

        nauseaIfHungry(player)
    }

    @EventHandler(ignoreCancelled = true)
    fun on(event: PlayerJoinEvent) {
        if (!isBeingWatched.add(event.player.uniqueId)) return

        nauseaIfHungry(event.player)
    }

    @EventHandler(ignoreCancelled = true)
    fun on(event: PlayerQuitEvent) {
        isBeingWatched.remove(event.player.uniqueId)
    }

    private fun nauseaIfHungry(player: Player) {
        if (player.uniqueId !in isBeingWatched) return
        if (!player.isOnline || !player.isValid) return
        if (player.foodLevel > typeSafeOptions.minFoodLevel) {
            isBeingWatched.remove(player.uniqueId)
            return
        }
        player.addPotionEffect(
            PotionEffect(
                PotionEffectType.NAUSEA,
                200, // 10 seconds
                0, // Level 1t
                false,
                false,
                false
            )
        )
        delay(100) {
            nauseaIfHungry(player)
        }
    }
}
