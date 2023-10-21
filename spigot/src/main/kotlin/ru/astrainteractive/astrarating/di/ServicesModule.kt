package ru.astrainteractive.astrarating.di

import org.bstats.bukkit.Metrics
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.filemanager.SpigotFileManager
import ru.astrainteractive.astrarating.AstraRating
import ru.astrainteractive.astrarating.event.EventManager
import ru.astrainteractive.astrarating.model.EmpireConfig
import ru.astrainteractive.astrarating.plugin.PluginTranslation
import ru.astrainteractive.klibs.kdi.Factory
import ru.astrainteractive.klibs.kdi.Lateinit
import ru.astrainteractive.klibs.kdi.Reloadable
import ru.astrainteractive.klibs.kdi.Single

interface ServicesModule {
    // Core
    val plugin: Lateinit<AstraRating>

    // Services
    val bstats: Factory<Metrics>
    val eventListener: Single<EventListener>
    val dispatchers: Single<BukkitDispatchers>
    val scope: Single<AsyncComponent>
    val configFileManager: Single<SpigotFileManager>
    val config: Reloadable<EmpireConfig>
    val translation: Reloadable<PluginTranslation>
    val eventManager: Factory<EventManager>
}
