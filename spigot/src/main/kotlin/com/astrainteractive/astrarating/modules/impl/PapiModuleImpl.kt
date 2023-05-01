package com.astrainteractive.astrarating.modules.impl

import com.astrainteractive.astrarating.domain.api.CachedApi
import com.astrainteractive.astrarating.integrations.papi.di.PapiModule
import com.astrainteractive.astrarating.modules.RootModule
import com.astrainteractive.astrarating.plugin.EmpireConfig
import ru.astrainteractive.astralibs.Dependency
import ru.astrainteractive.astralibs.getValue

object PapiModuleImpl : PapiModule {
    private val rootModule: RootModule by RootModuleImpl

    override val cachedApi: Dependency<CachedApi> = rootModule.cachedApi
    override val config: Dependency<EmpireConfig> = rootModule.config
}
