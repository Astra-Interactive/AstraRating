package ru.astrainteractive.astrarating.integration.papi.di

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.astrarating.api.rating.api.CachedApi
import ru.astrainteractive.astrarating.model.EmpireConfig

interface PapiModule {
    val cachedApi: CachedApi
    val config: EmpireConfig
    val scope: CoroutineScope
}
