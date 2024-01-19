package ru.astrainteractive.astrarating.event.di

import kotlinx.coroutines.CoroutineScope
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astrarating.api.rating.api.RatingDBApi
import ru.astrainteractive.astrarating.core.EmpireConfig
import ru.astrainteractive.astrarating.core.PluginTranslation
import ru.astrainteractive.astrarating.di.RootModule
import ru.astrainteractive.klibs.kdi.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

interface EventDependencies {
    val configDependency: EmpireConfig
    val apiDependency: RatingDBApi
    val translationDependency: PluginTranslation
    val scope: CoroutineScope
    val eventListener: EventListener
    val plugin: JavaPlugin
    val dispatchers: KotlinDispatchers
    val translationContext: KyoriComponentSerializer

    class Default(rootModule: RootModule) : EventDependencies {

        override val configDependency by rootModule.coreModule.config
        override val apiDependency = rootModule.apiRatingModule.ratingDBApi
        override val translationDependency by rootModule.coreModule.translation
        override val scope by rootModule.coreModule.scope
        override val eventListener by rootModule.bukkitModule.eventListener
        override val plugin by rootModule.bukkitModule.plugin
        override val dispatchers = rootModule.coreModule.dispatchers
        override val translationContext: KyoriComponentSerializer by rootModule.bukkitModule.kyoriComponentSerializer
    }
}
