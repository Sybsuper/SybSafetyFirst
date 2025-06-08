package com.sybsuper.sybsafetyfirst.modules

import com.sybsuper.sybsafetyfirst.SybSafetyFirst
import kotlinx.serialization.Serializable
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector
import java.util.LinkedList
import kotlin.collections.ArrayDeque
import kotlin.math.roundToInt
import kotlin.random.Random

class BadAirCaves : Module {
    override val description: String = "Poisonous air clouds can appear in caves, causing damage to players."
    override var options: ModuleOptions = BadAirCavesOptions()
    val typeSafeOptions
        get() = options as? BadAirCavesOptions ?: error("options are not of type BadAirCavesOptions")

    @Serializable
    data class BadAirCavesOptions(
        override var enabled: Boolean = true,
        var damageAmount: Double = 1.0,
        var applyPoison: Boolean = true,
        /**
         * If true, the gas will be considered heavy, meaning it will stay close to the ground.
         */
        var heavyGas: Boolean = true,
        /**
         * Gas cloud radius in blocks.
         * Decreasing this increases realism but also increases server load.
         * The Minimum value is 1.
         * Would not recommend going over 16.
         */
        var gasCloudRadius: Int = 4,
        /**
         * The distance the gas can spread from the edge of the gas cloud.
         * Should not be higher than the gas cloud radius, or there will be gaps in the gas cloud.
         */
        var poisonDurationTicks: Int = 100,
        var poisonLevel: Int = 1,
        var particle: Particle = Particle.WITCH,
        var particleCountPerCloudPart: Int = 16,
        var creationChancePerSecond: Double = 0.001,
        var cloudMaxSize: Int = 300,
        var particleVisibilityRange: Float = 32f,
        var spreadsPerCloudPerSecond: Int = 5,
        /**
         * Set to 0 to disable decay.
         */
        var decayAfterTicks: Int = 120 * 20,
    ) : ModuleOptions

    val interval = 20L // 1 second in ticks
    var currentTimeTicks: Long = 0L

    val clouds = mutableListOf<GasCloud>()
    val task = Runnable {
        spawnRandomClouds()
        spread()
        decay(interval)
        checkDamageAndShowParticles()
    }

    private fun spawnRandomClouds() {
        Bukkit.getOnlinePlayers().forEach {
            if (it.location.y > 32) return@forEach
            if (it.location.block.lightFromSky != 0.toByte()) return@forEach // Only spawn clouds in caves

            if (Random.nextDouble() < typeSafeOptions.creationChancePerSecond) {
                val playerLocation = it.location.clone().add(0.0, 3.0, 0.0)
                // Spawn a cloud at the player's location
                spawnCloud(playerLocation)
            }
        }
    }

    private fun spawnCloud(location: Location) {
        val cloud = GasCloud(location)
        cloud.init(this)
        clouds.add(cloud)
    }

    private fun decay(interval: Long) {
        if (typeSafeOptions.decayAfterTicks == 0) return
        currentTimeTicks += interval
        clouds.removeIf { cloud ->
            cloud.points.removeIf { point ->
                val removed = currentTimeTicks - point.createdAtTicks > typeSafeOptions.decayAfterTicks
                if (removed) remove(point)
                removed
            }
            cloud.points.isEmpty()
        }
    }

    private fun checkDamageAndShowParticles() {
        val players = Bukkit.getOnlinePlayers()
        players.forEach {
            checkClouds(it)
            sendParticles(it)
        }
    }

    private fun sendParticles(player: Player) {
        val particleVisibilityRange = typeSafeOptions.particleVisibilityRange
        val particleVisibilityRangeSquared = particleVisibilityRange * particleVisibilityRange
        val center = player.location
        val chunkRadius = (particleVisibilityRange / 16).roundToInt() + 1
        for (x in -chunkRadius..chunkRadius) {
            for (z in -chunkRadius..chunkRadius) {
                val chunk = center.world.getChunkAt(center.clone().add(Vector(x, 0, z).multiply(16)))
                if (!chunk.isLoaded) continue
                chunkMap[chunk]?.forEach { point ->
                    if (center.distanceSquared(point.location) <= particleVisibilityRangeSquared) {
                        val spread = typeSafeOptions.gasCloudRadius / 2
                        player.spawnParticle(
                            typeSafeOptions.particle,
                            point.location,
                            typeSafeOptions.particleCountPerCloudPart,
                            spread.toDouble(),
                            spread.toDouble(),
                            spread.toDouble(),
                            0.0
                        )
                    }
                }
            }
        }
    }

    private fun checkClouds(player: Player) {
        val chunks = mutableListOf<Chunk>()
        val playerLoc = player.location
        val world = player.world
        val chunkSize = 16
        for (x in (-1..1)) {
            for (z in (-1..1)) {
                chunks.add(
                    world.getChunkAt(
                        playerLoc
                            .clone()
                            .add(Vector(x, 0, z).multiply(chunkSize))
                    )
                )
            }
        }

        chunks.forEach { chunk ->
            chunkMap[chunk]?.forEach { point ->
                if (point.isInCloud(playerLoc)) {
                    if (typeSafeOptions.applyPoison) {
                        player.addPotionEffect(
                            PotionEffect(
                                PotionEffectType.POISON,
                                typeSafeOptions.poisonDurationTicks,
                                typeSafeOptions.poisonLevel - 1,
                                true,
                                false
                            )
                        )
                    }
                    player.damage(typeSafeOptions.damageAmount)
                }
            }
        }

    }

    override fun onEnable() {
        Bukkit.getScheduler().runTaskTimer(
            SybSafetyFirst.instance,
            task,
            0L,
            interval
        )
    }

    override fun onDisable() {
        Bukkit.getScheduler().cancelTasks(SybSafetyFirst.instance)
    }

    private fun spread() {
        clouds.forEach { cloud ->
            repeat(typeSafeOptions.spreadsPerCloudPerSecond) {
                cloud.spread(this)
            }
        }
    }


    class GasCloud(
        val center: Location,
        val points: MutableList<GasCloudPoint> = mutableListOf(),
    ) {
        val queue = ArrayDeque<GasCloudPoint>()

        fun spread(module: BadAirCaves): Unit = module.run {
            if (points.size >= typeSafeOptions.cloudMaxSize) return
            if (queue.isEmpty()) return

            val point = queue.removeFirst()

            val actualLocation = point.location.clone()
            // discover new points around the current point to spread to
            spread(point, actualLocation)
            if (!isValidSpreadLocation(actualLocation)) return
            // add the point to the cloud and to hitreg
            add(point)
            points.add(point)
        }

        private fun BadAirCaves.spread(
            point: GasCloudPoint,
            actualLocation: Location
        ) {
            val dx = point.direction.x.roundToInt()
            point.direction.y.roundToInt()
            val dz = point.direction.z.roundToInt()
            val possibleDirections = (-1..1).flatMap { rdx ->
                (-1..1).flatMap { rdy ->
                    (-1..1).mapNotNull { rdz ->
                        if (dx == -rdx || dz == -rdz) return@mapNotNull null
                        if (rdx == 0 && rdy == 0 && rdz == 0) return@mapNotNull null
                        Vector(rdx, rdy, rdz)
                    }
                }
            }
            possibleDirections.forEach { direction ->
                val newLocation = actualLocation.clone()
                moveWhileValid(
                    newLocation,
                    direction,
                    typeSafeOptions.gasCloudRadius,
                    ::isValidSpreadLocation
                )
                if (typeSafeOptions.heavyGas) {
                    moveWhileValid(
                        actualLocation,
                        Vector(0, -1, 0),
                        typeSafeOptions.gasCloudRadius,
                        ::isValidSpreadLocation
                    )
                }
                if (isValidSpreadLocation(newLocation)) {
                    val newPoint = GasCloudPoint(
                        newLocation,
                        typeSafeOptions.gasCloudRadius.toFloat(),
                        currentTimeTicks,
                        direction
                    )
                    if (points.any { it.isInCloud(newLocation) }) return@forEach
                    queue.add(newPoint)
                }
            }
        }

        fun init(module: BadAirCaves) = module.run {
            val initialPoint = GasCloudPoint(
                center.clone(),
                typeSafeOptions.gasCloudRadius.toFloat(),
                currentTimeTicks,
                // Initial direction can be arbitrary
                Vector(0.0, 1.0, 0.0)
            )
            queue.add(initialPoint)
            add(initialPoint)
            points.add(initialPoint)
        }
    }

    /**
     * Move max [steps] into direction [vector] starting at [location] while the location is [valid].
     *
     * @param location Starting location, which will be mutated.
     * @param vector Direction vector to move in
     * @param steps Maximum number of steps to move
     * @param isValid Function to check if the new location is valid
     * @return Whether the movement was stopped because the next location was invalid.
     */
    fun moveWhileValid(location: Location, vector: Vector, steps: Int, isValid: (Location) -> Boolean): Boolean {
        repeat(steps) {
            location.add(vector)
            if (!isValid(location)) {
                location.subtract(vector)
                return true
            }
        }
        return false
    }


    private fun isValidSpreadLocation(newLocation: Location): Boolean =
        (newLocation.block.type == Material.AIR || newLocation.block.type == Material.CAVE_AIR)

    class GasCloudPoint(
        val location: Location,
        val size: Float,
        val createdAtTicks: Long,
        val direction: Vector,
    ) {
        fun isInCloud(location: Location): Boolean {
            return location.x in (this.location.x - size..this.location.x + size) &&
                    location.y in (this.location.y - size..this.location.y + size) &&
                    location.z in (this.location.z - size..this.location.z + size)
        }
    }

    val chunkMap = mutableMapOf<Chunk, LinkedList<GasCloudPoint>>()

    private fun add(point: GasCloudPoint) {
        chunkMap.computeIfAbsent(point.location.chunk) { LinkedList() }.apply {
            add(point)
        }
    }

    private fun remove(point: GasCloudPoint) {
        chunkMap[point.location.chunk]?.remove(point)
    }
}