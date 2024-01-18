package ru.astrainteractive.astrarating.integration.papi.di

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.astrarating.api.rating.api.CachedApi
import ru.astrainteractive.astrarating.core.EmpireConfig

internal interface PapiDependencies {
    val cachedApi: CachedApi
    val config: EmpireConfig
    val scope: CoroutineScope
}
