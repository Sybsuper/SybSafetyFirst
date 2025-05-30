package com.sybsuper.sybsafetyfirst.utils

import com.sybsuper.sybsafetyfirst.SybSafetyFirst
import org.bukkit.Bukkit

fun delay(ticks: Long, action: () -> Unit) {
    if (ticks <= 0) {
        action()
        return
    }
    Bukkit.getScheduler().runTaskLater(
        SybSafetyFirst.instance,
        action,
        ticks
    )
}