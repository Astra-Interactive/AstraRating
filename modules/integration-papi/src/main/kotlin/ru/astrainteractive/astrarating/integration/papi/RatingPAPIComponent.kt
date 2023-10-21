package ru.astrainteractive.astrarating.integration.papi

import ru.astrainteractive.astrarating.integration.papi.di.PapiDependencies

class RatingPAPIComponent(dependencies: PapiDependencies) {
    private val expansion = RatingPAPIExpansion(dependencies)
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
