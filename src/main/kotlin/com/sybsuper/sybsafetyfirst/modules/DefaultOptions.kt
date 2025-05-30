package com.sybsuper.sybsafetyfirst.modules

import kotlinx.serialization.Serializable

@Serializable
data class DefaultOptions(
    override val enabled: Boolean = true
) : ModuleOptions
