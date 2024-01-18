package ru.astrainteractive.astrarating.integration.papi.di

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astrarating.api.rating.api.CachedApi
import ru.astrainteractive.astrarating.core.EmpireConfig
import ru.astrainteractive.astrarating.integration.papi.RatingPAPILifecycle
import ru.astrainteractive.klibs.kdi.Dependency
import ru.astrainteractive.klibs.kdi.getValue

interface PapiModule {
    val ratingPAPILifecycle: Lifecycle

    class Default(
        cachedApi: CachedApi,
        config: Dependency<EmpireConfig>,
        scope: CoroutineScope
    ) : PapiModule {
        private val papiDependencies = object : PapiDependencies {
            override val cachedApi: CachedApi = cachedApi
            override val config by config
            override val scope: CoroutineScope = scope
        }
        override val ratingPAPILifecycle: Lifecycle by lazy {
            RatingPAPILifecycle(papiDependencies)
        }
    }
}
