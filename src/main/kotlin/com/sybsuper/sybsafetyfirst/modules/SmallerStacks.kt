package com.sybsuper.sybsafetyfirst.modules

import kotlinx.serialization.Serializable
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.ItemStack

class SmallerStacks : Module {
    override val description: String = "Reduces the maximum stack size of items."
    override var options: ModuleOptions = SmallerStacksOptions()
    val typeSafeOptions
        get() = (options as? SmallerStacksOptions)
            ?: error("Options are not of type SmallerStacksOptions")

    @Serializable
    data class SmallerStacksOptions(
        override var enabled: Boolean = true,
        /**
         * The maximum stack size for all items.
         */
        var maxStackSize: Int = 31
    ) : ModuleOptions

    fun patchItem(item: ItemStack?) {
        val meta = item?.itemMeta ?: return
        val maxStackSize = if (meta.hasMaxStackSize()) {
            meta.maxStackSize
        } else {
            item.type.maxStackSize
        }
        meta.setMaxStackSize(maxStackSize.coerceAtMost(typeSafeOptions.maxStackSize))
        item.itemMeta = meta
    }

    @EventHandler(ignoreCancelled = true)
    fun on(event: InventoryClickEvent) {
        patchItem(event.currentItem ?: return)
    }

    @EventHandler(ignoreCancelled = true)
    fun on(event: PlayerAttemptPickupItemEvent) {
        patchItem(event.item.itemStack)
    }

    @EventHandler(ignoreCancelled = true)
    fun on(event: PlayerSwapHandItemsEvent) {
        patchItem(event.mainHandItem ?: return)
    }

    @EventHandler(ignoreCancelled = true)
    fun on(event: InventoryMoveItemEvent) {
        patchItem(event.item ?: return)
    }
}