package ru.astrainteractive.astrarating.event.di

import kotlinx.coroutines.CoroutineScope
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astrarating.api.rating.api.RatingDBApi
import ru.astrainteractive.astrarating.api.rating.di.ApiRatingModule
import ru.astrainteractive.astrarating.core.EmpireConfig
import ru.astrainteractive.astrarating.core.PluginTranslation
import ru.astrainteractive.astrarating.core.di.CoreModule
import ru.astrainteractive.astrarating.di.BukkitModule
import ru.astrainteractive.klibs.kdi.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

internal interface EventDependencies {
    val configDependency: EmpireConfig
    val apiDependency: RatingDBApi
    val translationDependency: PluginTranslation
    val scope: CoroutineScope
    val eventListener: EventListener
    val plugin: JavaPlugin
    val dispatchers: KotlinDispatchers
    val translationContext: KyoriComponentSerializer

    class Default(
        coreModule: CoreModule,
        apiRatingModule: ApiRatingModule,
        bukkitModule: BukkitModule
    ) : EventDependencies {

        override val configDependency by coreModule.config
        override val apiDependency = apiRatingModule.ratingDBApi
        override val translationDependency by coreModule.translation
        override val scope by coreModule.scope
        override val eventListener by bukkitModule.eventListener
        override val plugin by bukkitModule.plugin
        override val dispatchers = coreModule.dispatchers
        override val translationContext: KyoriComponentSerializer by bukkitModule.kyoriComponentSerializer
    }
}
