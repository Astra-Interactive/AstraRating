package ru.astrainteractive.astrarating.integration.papi.di

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.astrarating.data.dao.RatingCachedDao
import ru.astrainteractive.astrarating.integration.papi.model.PapiConfig

internal interface PapiDependencies {
    val ratingCachedDao: RatingCachedDao
    val papiConfiguration: PapiConfig
    val scope: CoroutineScope

    class Default(
        override val ratingCachedDao: RatingCachedDao,
        private val getPapiConfiguration: () -> PapiConfig,
        override val scope: CoroutineScope
    ) : PapiDependencies {
        override val papiConfiguration: PapiConfig
            get() = getPapiConfiguration.invoke()
    }
}
