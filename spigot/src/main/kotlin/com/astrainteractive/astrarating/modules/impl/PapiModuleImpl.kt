package com.astrainteractive.astrarating.modules.impl

import com.astrainteractive.astrarating.integrations.papi.di.PapiModule
import com.astrainteractive.astrarating.modules.RootModule
import ru.astrainteractive.klibs.kdi.getValue

object PapiModuleImpl : PapiModule {
    private val rootModule: RootModule by RootModuleImpl

    override val cachedApi by rootModule.cachedApi
    override val config by rootModule.config
    override val scope by rootModule.scope
}
