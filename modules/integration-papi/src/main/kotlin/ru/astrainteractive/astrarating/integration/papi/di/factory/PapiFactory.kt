package ru.astrainteractive.astrarating.integration.papi.di.factory

import ru.astrainteractive.astralibs.expansion.PlaceholderExpansionFacade
import ru.astrainteractive.astrarating.integration.papi.di.PapiDependencies
import ru.astrainteractive.astrarating.integration.papi.placeholder.expansion.RatingPlaceholderExpansion

internal class PapiFactory(
    private val dependencies: PapiDependencies
) {
    fun create(): PlaceholderExpansionFacade {
        return RatingPlaceholderExpansion(
            dependencies = dependencies
        )
    }
}
