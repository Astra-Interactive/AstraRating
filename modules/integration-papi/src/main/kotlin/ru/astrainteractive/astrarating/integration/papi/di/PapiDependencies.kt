package ru.astrainteractive.astrarating.integration.papi.di

import ru.astrainteractive.astrarating.data.dao.RatingCachedDao
import ru.astrainteractive.astrarating.integration.papi.model.PapiConfig

internal interface PapiDependencies {
    val ratingCachedDao: RatingCachedDao
    val papiConfiguration: PapiConfig

    class Default(
        override val ratingCachedDao: RatingCachedDao,
        private val getPapiConfiguration: () -> PapiConfig
    ) : PapiDependencies {
        override val papiConfiguration: PapiConfig
            get() = getPapiConfiguration.invoke()
    }
}
