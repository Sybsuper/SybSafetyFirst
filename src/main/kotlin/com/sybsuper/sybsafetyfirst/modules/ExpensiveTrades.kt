package com.sybsuper.sybsafetyfirst.modules

import kotlinx.serialization.Serializable
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.VillagerAcquireTradeEvent
import org.bukkit.event.entity.VillagerReplenishTradeEvent

class ExpensiveTrades : Module {
    override val description: String = "Trades will become expensive very quickly."
    override var options: ModuleOptions = ExpensiveTradesOptions()
    val typeSafeOptions
        get() = (options as? ExpensiveTradesOptions)
            ?: error("Options are not of type ExpensiveTradesOptions")

    @Serializable
    data class ExpensiveTradesOptions(
        override var enabled: Boolean = true,
        /**
         * Multiplier for the price of trades.
         * Higher values make the price changer more quickly.
         * Note this price change on itself goes both ways, meaning non-frequent trades (low demand) would be very cheap.
         * Use the [startingDemand] and [restockMinimumDemand] options to control the demand for trades. Where high demand means high prices.
         */
        var priceMultiplierMultiplier: Float = 3f,
        /**
         * High demand for trades means higher prices.
         * This only affects the initial demand for trades.
         * To keep high prices, the demand has to stay high by trading frequently.
         * @see restockMinimumDemand
         */
        var startingDemand: Int = 5,
        /**
         * The minimum demand for trades to be restocked.
         * If the demand is below this value, the recipe will not be restocked.
         * This is to prevent trades from becoming too cheap.
         */
        var restockMinimumDemand: Int = 5,
        /**
         * If true, the recipe will ignore discounts from villagers.
         * This should be set to true to ensure that trades remain expensive.
         * This ignores discounts from the hero of the village effect, and curing a zombie villager.
         */
        var ignoreDiscounts: Boolean = true,

        ) : ModuleOptions

    @EventHandler(ignoreCancelled = true)
    fun on(e: VillagerAcquireTradeEvent) {
        e.recipe.priceMultiplier *= typeSafeOptions.priceMultiplierMultiplier
        e.recipe.demand += typeSafeOptions.startingDemand
        if (typeSafeOptions.ignoreDiscounts)
            e.recipe.setIgnoreDiscounts(true)
    }

    @EventHandler(ignoreCancelled = true)
    fun on(e: VillagerReplenishTradeEvent) {
        if (e.recipe.demand < typeSafeOptions.restockMinimumDemand)
            e.recipe.demand = typeSafeOptions.restockMinimumDemand
    }
}