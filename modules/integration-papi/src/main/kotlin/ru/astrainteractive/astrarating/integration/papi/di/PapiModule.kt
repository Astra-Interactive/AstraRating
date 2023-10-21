package ru.astrainteractive.astrarating.integration.papi.di

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.astrarating.api.rating.api.CachedApi
import ru.astrainteractive.astrarating.integration.papi.RatingPAPIComponent
import ru.astrainteractive.astrarating.model.EmpireConfig
import ru.astrainteractive.klibs.kdi.Dependency
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue

interface PapiModule {
    val ratingPAPIComponent: RatingPAPIComponent

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
        override val ratingPAPIComponent: RatingPAPIComponent by Single {
            RatingPAPIComponent(papiDependencies)
        }
    }
}
