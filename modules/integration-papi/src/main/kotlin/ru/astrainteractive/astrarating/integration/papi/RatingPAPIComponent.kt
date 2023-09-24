package ru.astrainteractive.astrarating.integration.papi

import ru.astrainteractive.astrarating.integration.papi.di.PapiModule

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
