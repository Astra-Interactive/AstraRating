package ru.astrainteractive.astrarating.event.di

import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astrarating.di.RootModule
import ru.astrainteractive.klibs.kdi.getValue

class EventModuleImpl(rootModule: RootModule) : EventModule {

    override val configDependency by rootModule.servicesModule.config
    override val apiDependency = rootModule.apiRatingModule.ratingDBApi
    override val translationDependency by rootModule.servicesModule.translation
    override val scope by rootModule.servicesModule.scope
    override val eventListener by rootModule.servicesModule.eventListener
    override val plugin by rootModule.servicesModule.plugin
    override val dispatchers by rootModule.servicesModule.dispatchers
    override val translationContext: KyoriComponentSerializer by rootModule.servicesModule.componentSerializer
}
