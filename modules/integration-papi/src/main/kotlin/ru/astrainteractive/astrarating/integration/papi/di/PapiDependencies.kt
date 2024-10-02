package ru.astrainteractive.astrarating.integration.papi.di

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.astrarating.api.rating.api.CachedApi
import ru.astrainteractive.astrarating.integration.papi.model.PapiConfig

internal interface PapiDependencies {
    val cachedApi: CachedApi
    val papiConfiguration: PapiConfig
    val scope: CoroutineScope

    class Default(
        override val cachedApi: CachedApi,
        private val getPapiConfiguration: () -> PapiConfig,
        override val scope: CoroutineScope
    ) : PapiDependencies {
        override val papiConfiguration: PapiConfig
            get() = getPapiConfiguration.invoke()
    }
}
