package com.sybsuper.sybsafetyfirst.modules

import kotlinx.serialization.Serializable
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerPortalEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.util.Vector
import kotlin.random.Random

class NetherPortalsDestabilize : Module {
    override val description: String = "Destabilizes Nether portals, making them less reliable."
    override var options: ModuleOptions = NetherPortalsDestabilizeOptions()
    val typeSafeOptions
        get() = options as? NetherPortalsDestabilizeOptions
            ?: error("options are not of type NetherPortalsDestabilizeOptions")

    @Serializable
    data class NetherPortalsDestabilizeOptions(
        override var enabled: Boolean = true,
        /**
         * The chance that a Nether portal will destabilize when used.
         * 0.0 means no destabilization, 1.0 means always destabilizes.
         */
        var destabilizationChance: Float = 0.5f,
        /**
         * Destabilization distance in blocks.
         * 5000 means the portal will be created maximally 5000 blocks away from the original location on each axis (X, Z).
         */
        var destabilizationRadius: Float = 5000f,
        /**
         * Minimessage to display when a portal is destabilized.
         * Set to an empty string to disable the message.
         */
        var destabilizationMessage: String = "<red>Warning: The portal you used was unstable!"
    ) : ModuleOptions

    @EventHandler(ignoreCancelled = true)
    fun on(event: PlayerPortalEvent) {
        if (event.cause != PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) return
        if (!event.canCreatePortal) return
        if (Random.nextFloat() >= typeSafeOptions.destabilizationChance) return
        event.to = event.to.subtract(
            Vector(
                Random.nextFloat() - 0.5f, // Random offset in X direction
                0.0f, // No offset in Y direction
                Random.nextFloat() - 0.5f  // Random offset in Z direction
            ).multiply(typeSafeOptions.destabilizationRadius)
        )
        if (typeSafeOptions.destabilizationMessage.isNotBlank()) {
            event.player.sendMessage(
                MiniMessage.miniMessage().deserialize(typeSafeOptions.destabilizationMessage)
            )
        }
    }
}