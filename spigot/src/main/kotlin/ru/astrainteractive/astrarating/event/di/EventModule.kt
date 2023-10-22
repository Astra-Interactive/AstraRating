package ru.astrainteractive.astrarating.event.di

import kotlinx.coroutines.CoroutineScope
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astrarating.api.rating.api.RatingDBApi
import ru.astrainteractive.astrarating.model.EmpireConfig
import ru.astrainteractive.astrarating.model.PluginTranslation

interface EventModule {
    val configDependency: EmpireConfig
    val apiDependency: RatingDBApi
    val translationDependency: PluginTranslation
    val scope: CoroutineScope
    val eventListener: EventListener
    val plugin: JavaPlugin
    val dispatchers: BukkitDispatchers
    val translationContext: KyoriComponentSerializer
}
