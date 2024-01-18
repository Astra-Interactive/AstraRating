package ru.astrainteractive.astrarating.integration.papi

import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astrarating.integration.papi.di.PapiDependencies

internal class RatingPAPILifecycle(dependencies: PapiDependencies) : Lifecycle {
    private val expansion = RatingPAPIExpansion(dependencies)

    override fun onEnable() {
        if (expansion.isRegistered) {
            expansion.unregister()
        }
        expansion.register()
    }

    override fun onDisable() {
        expansion.unregister()
    }
}
