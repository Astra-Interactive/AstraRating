package com.astrainteractive.astrarating.modules.impl

import com.astrainteractive.astrarating.events.di.EventModule
import com.astrainteractive.astrarating.modules.RootModule
import ru.astrainteractive.klibs.kdi.getValue

object EventModuleImpl : EventModule {
    private val rootModule: RootModule by RootModuleImpl

    override val configDependency by rootModule.config
    override val apiDependency by rootModule.dbApi
    override val translationDependency by rootModule.translation
    override val scope by rootModule.scope
    override val eventListener by rootModule.eventListener
    override val plugin by rootModule.plugin
    override val dispatchers by rootModule.dispatchers
}
