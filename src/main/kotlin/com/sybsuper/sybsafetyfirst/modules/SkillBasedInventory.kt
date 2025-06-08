package com.sybsuper.sybsafetyfirst.modules

import com.sybsuper.sybsafetyfirst.utils.delay
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class SkillBasedInventory : Module {
    override val description: String = "Unlock inventory slots based on experience levels."
    override var options: ModuleOptions = SkillBasedInventoryOptions()
    val typeSafeOptions
        get() = options as? SkillBasedInventoryOptions ?: error("options are not of type SkillBasedInventoryOptions")

    @Serializable
    data class SkillBasedInventoryOptions(
        override var enabled: Boolean = true,
        /**
         * The number of levels required to unlock each additional slot.
         * For example, if this is set to 2, the player will need 2 levels to unlock the first additional slot,
         * 4 levels for the second, and so on.
         */
        var levelsPerSlot: Int = 2,
        /**
         * The number of slots the player starts with.
         */
        var startingSlots: Int = 9,
        /**
         * The last affected slot index.
         * Setting this to 35 means the player does not have to unlock their armor slots or offhand.
         * Setting this to 39 means the player does not have to unlock their offhand slot, but has to unlock their armor slots.
         * Setting this to 40 means the player has to unlock their offhand slot and armor slots.
         */
        var lastAffectedSlotIndex: Int = 35,
    ) : ModuleOptions

    val fillerItem = ItemStack(Material.GRAY_STAINED_GLASS_PANE).apply {
        itemMeta = itemMeta?.apply {
            displayName(Component.text("Locked Slot", NamedTextColor.GRAY))
            addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
            isUnbreakable = true
        }
    }

    override fun onEnable() {
        Bukkit.getOnlinePlayers().forEach { player ->
            applyInventory(player)
        }
    }

    override fun onDisable() {
        Bukkit.getOnlinePlayers().forEach { player ->
            freeInventory(player)
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun on(event: PlayerAttemptPickupItemEvent) {
        val player = event.player
        val item = event.item.itemStack
        if (item.isSimilar(fillerItem)) {
            event.isCancelled = true
            event.item.remove()
            return
        }
        applyInventory(player)
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun on(event: PlayerDeathEvent) {
        val player = event.entity
        freeInventory(player)
        event.drops.removeIf { it?.isSimilar(fillerItem) == true }
        event.itemsToKeep.removeIf { it?.isSimilar(fillerItem) == true }
    }

    @EventHandler(ignoreCancelled = true)
    fun on(event: PlayerJoinEvent) {
        applyInventory(event.player)
    }

    @EventHandler(ignoreCancelled = true)
    fun on(event: PlayerRespawnEvent) {
        applyInventory(event.player)
    }

    @EventHandler(ignoreCancelled = true)
    fun onJoin(event: PlayerRespawnEvent) {
        applyInventory(event.player)
    }

    @EventHandler(ignoreCancelled = true)
    fun on(event: PlayerLevelChangeEvent) {
        delay(1) {
            applyInventory(event.player)
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun on(event: InventoryClickEvent) {
        val inv = event.clickedInventory ?: return
        val slot = event.slot
        val clickedItem = inv.getItem(slot) ?: return
        if (clickedItem.isSimilar(fillerItem)) {
            event.isCancelled = true
            return
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun on(event: PlayerSwapHandItemsEvent) {
        val main: ItemStack? = event.mainHandItem
        if (main?.isSimilar(fillerItem) == true) {
            event.isCancelled = true
            return
        }
        val off: ItemStack? = event.offHandItem
        if (off?.isSimilar(fillerItem) == true) {
            event.isCancelled = true
            return
        }
    }

    private fun freeInventory(player: Player) {
        for (i in 0 until player.inventory.size) {
            if (player.inventory.getItem(i)?.isSimilar(fillerItem) == true) {
                player.inventory.setItem(i, ItemStack(Material.AIR))
            }
        }
        player.updateInventory()
    }

    @EventHandler(ignoreCancelled = true)
    fun on(event: PlayerInteractEvent) {
        if (event.item?.isSimilar(fillerItem) == true) {
            event.isCancelled = true
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun on(event: PlayerItemHeldEvent) {
        val inv = event.player.inventory
        val item = inv.getItem(event.newSlot)
        if (item?.isSimilar(fillerItem) == true) {
            event.isCancelled = true
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun on(event: PlayerDropItemEvent) {
        if (event.itemDrop.itemStack.isSimilar(fillerItem))
            event.isCancelled = true
    }

    private fun applyInventory(player: Player) {
        val inventory = player.inventory
        val availableSlots = (player.level / typeSafeOptions.levelsPerSlot) + typeSafeOptions.startingSlots
        for (i in 0 until inventory.size.coerceAtMost(typeSafeOptions.lastAffectedSlotIndex + 1)) {
            if (i >= availableSlots) {
                if (inventory.getItem(i)?.isSimilar(fillerItem) == true) continue
                player.dropItem(i)
                inventory.setItem(i, fillerItem)
            } else if (inventory.getItem(i)?.isSimilar(fillerItem) == true) {
                inventory.setItem(i, ItemStack(Material.AIR)) // Clear the filler item
            }
        }
        player.updateInventory()
    }
}