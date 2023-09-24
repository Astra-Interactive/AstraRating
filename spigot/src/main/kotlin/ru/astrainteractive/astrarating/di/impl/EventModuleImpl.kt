package ru.astrainteractive.astrarating.di.impl

import ru.astrainteractive.astrarating.di.RootModule
import ru.astrainteractive.astrarating.event.di.EventModule
import ru.astrainteractive.klibs.kdi.getValue

class EventModuleImpl(rootModule: RootModule) : EventModule {

    override val configDependency by rootModule.config
    override val apiDependency by rootModule.dbApi
    override val translationDependency by rootModule.translation
    override val scope by rootModule.scope
    override val eventListener by rootModule.eventListener
    override val plugin by rootModule.plugin
    override val dispatchers by rootModule.dispatchers
}
