package com.sybsuper.sybsafetyfirst.modules

import kotlinx.serialization.Serializable

@Serializable
sealed interface ModuleOptions {
    val enabled: Boolean
}