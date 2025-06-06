package com.sybsuper.sybsafetyfirst.modules

import kotlinx.serialization.Serializable
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.data.type.Bed
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerBedEnterEvent
import org.bukkit.event.player.PlayerInteractEvent

class IntentionalGameDesign : Module {
    override val description: String = "Beds explode in all dimensions."
    override var options: ModuleOptions = IntentionalGameDesignOptions()
    val typeSafeOptions
        get() = (options as? IntentionalGameDesignOptions)
            ?: error("Options are not of type IntentionalGameDesignOptions")

    @Serializable
    data class IntentionalGameDesignOptions(
        override var enabled: Boolean = true,
        /**
         * The power of the explosion caused by the bed.
         * Minecraft default for bed explosions is 5.0.
         */
        var explosionPower: Float = 5f
    ) : ModuleOptions

    private val explosions = mutableMapOf<Location, Long>()
        get() {
            val currentTime = System.currentTimeMillis()
            field.entries.removeIf { it.value < currentTime }
            return field
        }

    @EventHandler(ignoreCancelled = true)
    fun on(event: PlayerInteractEvent) {
        if (event.action != RIGHT_CLICK_BLOCK) return
        if (event.player.isSneaking) return
        val block = event.clickedBlock
        if (block?.blockData !is Bed) return
        if (!block.world.isBedWorks) return
        event.isCancelled = true
        explode(block)
    }

    private fun explode(block: Block) {
        val world = block.world
        explosions[block.location] = System.currentTimeMillis() + 150
        val otherBlock =
            block.blockData.let { bedData ->
                if (bedData !is Bed) return
                val direction = bedData.facing
                block.getRelative(
                    if (bedData.part == Bed.Part.HEAD) {
                        direction.oppositeFace
                    } else {
                        direction
                    }
                )
            }
        block.type = Material.AIR
        if (otherBlock.blockData is Bed) otherBlock.type = Material.AIR
        world.createExplosion(block.location, typeSafeOptions.explosionPower, true, true)
    }

    @EventHandler(ignoreCancelled = true)
    fun on(event: PlayerBedEnterEvent) {
        event.isCancelled = true
        val block = event.bed
        explode(block)
    }

    @EventHandler(ignoreCancelled = true)
    fun on(event: PlayerDeathEvent) {
        if (event.entity.lastDamageCause?.cause != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) return
        val damageLocation = event.entity.location
        explosions.forEach { (location, _) ->
            if (location.world != damageLocation.world) {
                return@forEach
            }
            if (location.distanceSquared(damageLocation) < 100.0) {
                event.deathMessage(
                    Component.translatable(
                        "death.attack.badRespawnPoint.message",
                        event.entity.displayName().hoverEvent(
                            HoverEvent.showEntity(
                                HoverEvent.ShowEntity.showEntity(
                                    Key.key("minecraft", "player"),
                                    event.entity.uniqueId,
                                    event.entity.displayName(),
                                )
                            )
                        ),
                        Component.text("[").append(
                            Component.translatable("death.attack.badRespawnPoint.link")
                        ).append(Component.text("]")).hoverEvent(
                            HoverEvent.showText(
                                Component.text("MCPE-28723")
                            )
                        ).clickEvent(ClickEvent.openUrl("https://bugs.mojang.com/browse/MCPE-28723"))
                    )
                )
            }
        }
    }
}