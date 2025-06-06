package com.sybsuper.sybsafetyfirst.modules

import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Creeper
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityTargetLivingEntityEvent

class FastCreepers : Module {
    override val description: String = "Makes creepers move and explode faster."
    override var options: ModuleOptions = FastCreepersOptions()
    val typeSafeOptions
        get() = (options as? FastCreepersOptions) ?: error("Options are not of type FastCreepersOptions")
    private val watchlist = mutableSetOf<Creeper>()

    @Serializable
    data class FastCreepersOptions(
        override var enabled: Boolean = true,
        /**
         * Speed at which the creeper moves towards its target.
         * Minecraft default is 0.25
         */
        var speed: Double = 0.5,
        /**
         * Duration in ticks before the creeper explodes.
         * Minecraft default is 30 ticks (1.5 seconds)
         */
        var fuseDuration: Int = 15,
        /**
         * Whether the creeper should jump towards its target.
         * If true, the creeper will jump after [jumpAfterTicks] ticks.
         */
        var jump: Boolean = true,
        /**
         * Number of ticks after which the creeper will jump towards its target.
         * If [jump] is false, this value is ignored.
         */
        var jumpAfterTicks: Int = 5,
        /**
         * Vertical velocity of the creeper when it jumps.
         * This is a multiplier for the jump height.
         */
        var jumpVerticalVelocity: Double = 0.5,
        /**
         * Horizontal velocity of the creeper when it jumps.
         * This is a multiplier for the jump distance.
         */
        var jumpHorizontalVelocity: Double = 0.42
    ) : ModuleOptions

    @EventHandler(ignoreCancelled = true)
    fun on(event: EntityTargetLivingEntityEvent) {
        val entity = event.entity
        if (entity !is Creeper) return
        val target = event.target
        if (target !is Player) return
        entity.maxFuseTicks = typeSafeOptions.fuseDuration
        entity.getAttribute(Attribute.MOVEMENT_SPEED)?.baseValue = typeSafeOptions.speed
        watchlist.add(entity)
    }

    val task: Runnable = Runnable {
        watchlist.removeIf { creeper -> creeper.isDead || !creeper.isValid || creeper.target !is Player }
        watchlist.forEach {
            it.run {
                if (fuseTicks > typeSafeOptions.jumpAfterTicks && isOnGround) {
                    jump(it)
                }
            }
        }
    }

    private fun jump(creeper: Creeper) {
        val entity = creeper
        val target = entity.target as? Player ?: return
        if (typeSafeOptions.jump) {
            val jumpVelocity = target.location.toVector().subtract(entity.location.toVector())
            jumpVelocity.y = 0.0
            jumpVelocity.normalize().multiply(typeSafeOptions.jumpHorizontalVelocity)
            jumpVelocity.y = typeSafeOptions.jumpVerticalVelocity
            entity.velocity = jumpVelocity
        }
    }

    override fun onEnable() {
        if (typeSafeOptions.jump) {
            Bukkit.getScheduler().runTaskTimer(
                com.sybsuper.sybsafetyfirst.SybSafetyFirst.instance,
                task,
                0L,
                1L
            )
        }
    }

    override fun onDisable() {
        if (typeSafeOptions.jump) {
            Bukkit.getScheduler().cancelTasks(com.sybsuper.sybsafetyfirst.SybSafetyFirst.instance)
        }
    }
}