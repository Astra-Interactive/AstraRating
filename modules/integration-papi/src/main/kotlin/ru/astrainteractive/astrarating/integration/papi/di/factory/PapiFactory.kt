package ru.astrainteractive.astrarating.integration.papi.di.factory

import ru.astrainteractive.astralibs.expansion.PlaceholderExpansionFacade
import ru.astrainteractive.astrarating.integration.papi.RatingPAPIExpansion
import ru.astrainteractive.astrarating.integration.papi.di.PapiDependencies

internal class PapiFactory(
    private val dependencies: PapiDependencies
) {
    fun create(): PlaceholderExpansionFacade {
        return RatingPAPIExpansion(
            dependencies = dependencies
        )
    }
}
