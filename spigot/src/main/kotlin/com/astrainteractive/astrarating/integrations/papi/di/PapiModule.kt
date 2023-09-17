package com.astrainteractive.astrarating.integrations.papi.di

import com.astrainteractive.astrarating.domain.api.CachedApi
import com.astrainteractive.astrarating.plugin.EmpireConfig
import kotlinx.coroutines.CoroutineScope

interface PapiModule {
    val cachedApi: CachedApi
    val config: EmpireConfig
    val scope: CoroutineScope
}
