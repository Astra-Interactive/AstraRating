package ru.astrainteractive.astrarating.event.di

import kotlinx.coroutines.CoroutineScope
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astrarating.api.rating.api.RatingDBApi
import ru.astrainteractive.astrarating.di.RootModule
import ru.astrainteractive.astrarating.model.EmpireConfig
import ru.astrainteractive.astrarating.model.PluginTranslation
import ru.astrainteractive.klibs.kdi.getValue

interface EventDependencies {
    val configDependency: EmpireConfig
    val apiDependency: RatingDBApi
    val translationDependency: PluginTranslation
    val scope: CoroutineScope
    val eventListener: EventListener
    val plugin: JavaPlugin
    val dispatchers: BukkitDispatchers
    val translationContext: KyoriComponentSerializer

    class Default(rootModule: RootModule) : EventDependencies {

        override val configDependency by rootModule.servicesModule.config
        override val apiDependency = rootModule.apiRatingModule.ratingDBApi
        override val translationDependency by rootModule.servicesModule.translation
        override val scope by rootModule.servicesModule.scope
        override val eventListener by rootModule.servicesModule.eventListener
        override val plugin by rootModule.servicesModule.plugin
        override val dispatchers by rootModule.servicesModule.dispatchers
        override val translationContext: KyoriComponentSerializer by rootModule.servicesModule.componentSerializer
    }
}
