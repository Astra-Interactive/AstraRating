package ru.astrainteractive.astrarating.integration.papi.di

import ru.astrainteractive.astrarating.data.dao.rating.api.RatingCache
import ru.astrainteractive.astrarating.integration.papi.model.PapiConfig

internal interface PapiDependencies {
    val ratingCache: RatingCache
    val papiConfiguration: PapiConfig

    class Default(
        override val ratingCache: RatingCache,
        private val getPapiConfiguration: () -> PapiConfig
    ) : PapiDependencies {
        override val papiConfiguration: PapiConfig
            get() = getPapiConfiguration.invoke()
    }
}
