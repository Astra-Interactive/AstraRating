package com.astrainteractive.astrarating.events.di

import com.astrainteractive.astrarating.domain.api.RatingDBApi
import com.astrainteractive.astrarating.plugin.EmpireConfig
import com.astrainteractive.astrarating.plugin.PluginTranslation
import kotlinx.coroutines.CoroutineScope
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.Dependency
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.events.EventListener

interface EventModule {
    val configDependency: Dependency<EmpireConfig>
    val apiDependency: Dependency<RatingDBApi>
    val translationDependency: Dependency<PluginTranslation>
    val scope: Dependency<CoroutineScope>
    val eventListener: Dependency<EventListener>
    val plugin: Dependency<JavaPlugin>
    val dispatchers: Dependency<BukkitDispatchers>
}
