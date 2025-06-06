package com.sybsuper.sybsafetyfirst.modules

import kotlinx.serialization.Serializable

@Serializable
data class DefaultOptions(
    override var enabled: Boolean = true
) : ModuleOptions
