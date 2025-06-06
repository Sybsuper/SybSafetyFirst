package com.sybsuper.sybsafetyfirst.modules

import kotlinx.serialization.Serializable
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.inventory.ItemStack

class WrongToolsHurt : Module {
    override val description: String = "Makes players take damage when using the wrong tool for a block."
    override var options: ModuleOptions = WrongToolsHurtOptions()
    val typeSafeOptions
        get() = options as? WrongToolsHurtOptions ?: error("options are not of type WrongToolsHurtOptions")

    @Serializable
    data class WrongToolsHurtOptions(
        override var enabled: Boolean = true,
        var damageAmount: Double = 1.0
    ) : ModuleOptions

    @EventHandler(ignoreCancelled = true)
    fun onDig(event: BlockDamageEvent) {
        if (event.player.gameMode == GameMode.CREATIVE) return
        val block = event.block
        val tool = event.player.inventory.itemInMainHand
        if (!block.type.isCorrectTool(tool)) {
            // If the tool is not correct, apply damage
            event.player.damage(typeSafeOptions.damageAmount)
        }
    }

    private val tools = arrayOf(
        Material.WOODEN_SWORD,
        Material.WOODEN_SHOVEL,
        Material.WOODEN_PICKAXE,
        Material.WOODEN_AXE,
        Material.WOODEN_HOE
    )

    private fun Material.isCorrectTool(tool: ItemStack?): Boolean {
        if (tool == null || tool.type == Material.AIR) return false
        val data = this.createBlockData()
        val currentSpeed = data.getDestroySpeed(tool)
        val minSpeed = tools.maxOfOrNull { data.getDestroySpeed(ItemStack(it)) } ?: 0.0f
        return currentSpeed >= minSpeed
    }
}
