package com.astrainteractive.astrarating.integrations.papi

import com.astrainteractive.astrarating.integrations.papi.di.PapiModule

class RatingPAPIComponent(module: PapiModule) {
    private val expansion = RatingPAPIExpansion(module)
    fun onEnable() {
        if (expansion.isRegistered) {
            expansion.unregister()
        }
        expansion.register()
    }

    fun onDisable() {
        expansion.unregister()
    }
}
