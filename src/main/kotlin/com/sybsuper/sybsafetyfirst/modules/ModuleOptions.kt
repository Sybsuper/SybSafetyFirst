package com.sybsuper.sybsafetyfirst.modules

import kotlinx.serialization.Serializable

@Serializable
sealed interface ModuleOptions {
    /**
     * Whether the module is enabled or not.
     * This is used to determine if the module should be loaded and its features activated.
     */
    var enabled: Boolean
        @Deprecated(
            "Use ModuleManager's API methods to manage module state.",
            ReplaceWith("ModuleManager.enableModule(this) or ModuleManager.disableModule(this)")
        )
        set
}