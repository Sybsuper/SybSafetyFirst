package com.sybsuper.sybsafetyfirst.modules

import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Creeper
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityTargetLivingEntityEvent

object FastCreepers : Module {
    override val name: String = "Fast Creepers"
    override val description: String = "Makes creepers move and explode faster."
    override var options: ModuleOptions = FastCreepersOptions()
    val typeSafeOptions
        get() = (options as? FastCreepersOptions) ?: error("Options are not of type FastCreepersOptions")
    private val watchlist = mutableSetOf<Creeper>()

    @Serializable
    data class FastCreepersOptions(
        override val enabled: Boolean = true,
        val speed: Double = 0.5,
        val fuseDuration: Int = 15,
        val jump: Boolean = false,
        val jumpAfterTicks: Int = 5,
        val jumpVerticalVelocity: Double = 0.5,
        val jumpHorizontalVelocity: Double = 0.42
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
                println("$fuseTicks, $maxFuseTicks, $isOnGround")
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
            println("Jumping creeper at ${entity.location} towards ${target.location}")
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