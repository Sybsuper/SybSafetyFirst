package com.sybsuper.sybsafetyfirst.modules

import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import org.bukkit.util.Vector
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

class WaterCurrent : Module {
    override val description: String =
        "Makes water currents more \"realistic\". Pushes entities in water towards the global current direction."
    override var options: ModuleOptions = WaterCurrentOptions()
    val typeSafeOptions
        get() = (options as? WaterCurrentOptions) ?: error("Options are not of type WaterCurrentOptions")

    @Serializable
    data class WaterCurrentOptions(
        override var enabled: Boolean = true,
        /**
         * The velocity at which entities in water are pushed.
         */
        var velocity: Double = 0.015,
        /**
         * The interval in milliseconds at which the direction of the current changes.
         */
        var directionChangeIntervalMs: Int = 10000,
        /**
         * The number of degrees the current direction changes per interval.
         */
        var directionChangeDegreesPerInterval: Double = 0.5,
        /**
         * The interval in ticks at which the current is applied to entities.
         * 20 ticks = 1 second, so 2 means 10 times per second.
         * Min: 1 tick, max: infinity.
         * Higher values mean less frequent updates, which can reduce server load but may make the current feel more "stuttering".
         * Higher values also mean that the player has more control over their movement, since setting the velocity overrides their movement.
         */
        var applyInterval: Long = 2
    ) : ModuleOptions

    private val currentDirection: Vector = Vector(1, 0, 0)
    private val nextDirection: Vector = Vector(0, 0, 1)
    private fun randomDirection(): Vector {
        return Vector(
            (Math.random() - 0.5) * 2,
            0.0,
            (Math.random() - 0.5) * 2
        ).normalize()
    }

    private fun interpolateDirection() {
        // modifies the currentDirection to be a little closer to the nextDirection, by rotating it slightly towards the nextDirection
        val from = hypot(currentDirection.x, currentDirection.z)
        val to = hypot(nextDirection.x, nextDirection.z)
        if (from < 0.0001 || to < 0.0001) return
        val fx = currentDirection.x / from
        val fz = currentDirection.z / from
        val tx = nextDirection.x / to
        val tz = nextDirection.z / to

        val dot = fx * tx + fz * tz
        val det = fx * tz - fz * tx
        val angle = atan2(det, dot)

        val maxChange = Math.toRadians(typeSafeOptions.directionChangeDegreesPerInterval)
        val clampedAngle = angle.coerceIn(-maxChange, maxChange)

        val cos = cos(clampedAngle)
        val sin = sin(clampedAngle)
        currentDirection.x = fx * cos - fz * sin
        currentDirection.z = fx * sin + fz * cos
    }

    private var lastChangeTime = 0L

    val task: Runnable = Runnable {
        // Interpolate the current direction towards the next direction
        interpolateDirection()
        val now = System.currentTimeMillis()
        if (now - lastChangeTime > typeSafeOptions.directionChangeIntervalMs) {
            nextDirection.copy(randomDirection())
            lastChangeTime = now
        }
        Bukkit.getWorlds().forEach { world ->
            world.entities.forEach { entity ->
                if (entity.isInWater) {
                    val velocity = currentDirection.clone().multiply(typeSafeOptions.velocity)
                    entity.velocity = entity.velocity.add(velocity)
                }
            }
        }
    }

    override fun onEnable() {
        Bukkit.getScheduler().runTaskTimer(
            com.sybsuper.sybsafetyfirst.SybSafetyFirst.instance,
            task,
            0L,
            typeSafeOptions.applyInterval
        )
    }

    override fun onDisable() {
        Bukkit.getScheduler().cancelTasks(com.sybsuper.sybsafetyfirst.SybSafetyFirst.instance)
    }
}