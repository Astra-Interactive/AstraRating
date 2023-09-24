package ru.astrainteractive.astrarating.di.impl

import ru.astrainteractive.astrarating.di.RootModule
import ru.astrainteractive.astrarating.integration.papi.di.PapiModule
import ru.astrainteractive.klibs.kdi.getValue

class PapiModuleImpl(rootModule: RootModule) : PapiModule {

    override val cachedApi by rootModule.cachedApi
    override val config by rootModule.config
    override val scope by rootModule.scope
}
