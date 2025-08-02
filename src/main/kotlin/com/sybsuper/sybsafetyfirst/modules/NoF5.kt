package com.sybsuper.sybsafetyfirst.modules

import com.sybsuper.sybsafetyfirst.SybSafetyFirst
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attributable
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class NoF5 : Module {
    override val description: String = "Disables other perspectives than first-person."
    override var options: ModuleOptions = DefaultOptions()
    val key = NamespacedKey(SybSafetyFirst.namespace, "no_f5")
    val modifier = AttributeModifier(
        key,
        -1.0,
        AttributeModifier.Operation.MULTIPLY_SCALAR_1
    )

    fun add(attributable: Attributable) {
        val hasModifier = attributable.getAttribute(Attribute.CAMERA_DISTANCE)?.getModifier(key) != null
        if (hasModifier) return
        attributable.getAttribute(Attribute.CAMERA_DISTANCE)?.addModifier(modifier)
    }

    fun remove(attributable: Attributable) {
        attributable.getAttribute(Attribute.CAMERA_DISTANCE)?.removeModifier(modifier)
    }

    override fun onEnable() {
        Bukkit.getOnlinePlayers().forEach { player ->
            add(player)
        }
    }

    override fun onDisable() {
        Bukkit.getOnlinePlayers().forEach { player ->
            remove(player)
        }
    }

    @EventHandler
    fun on(e: PlayerJoinEvent) {
        add(e.player)
    }

    @EventHandler
    fun on(e: PlayerQuitEvent) {
        remove(e.player)
    }
}