package com.astrainteractive.astrarating.integrations.papi.di

import com.astrainteractive.astrarating.domain.api.CachedApi
import com.astrainteractive.astrarating.plugin.EmpireConfig
import ru.astrainteractive.astralibs.Dependency

interface PapiModule {
    val cachedApi: Dependency<CachedApi>
    val config: Dependency<EmpireConfig>
}