package ru.astrainteractive.astrarating.di

import org.bstats.bukkit.Metrics
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.menu.event.DefaultInventoryClickEvent
import ru.astrainteractive.astralibs.permission.PermissionManager
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.astrarating.AstraRating
import ru.astrainteractive.astrarating.event.EventManager
import ru.astrainteractive.astrarating.model.EmpireConfig
import ru.astrainteractive.astrarating.model.PluginTranslation
import ru.astrainteractive.klibs.kdi.Factory
import ru.astrainteractive.klibs.kdi.Lateinit
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.Reloadable
import ru.astrainteractive.klibs.kdi.Single

interface ServicesModule {
    // Core
    val plugin: Lateinit<AstraRating>
    val inventoryClickEvent: Single<DefaultInventoryClickEvent>
    val componentSerializer: Single<KyoriComponentSerializer>

    // Services
    val bstats: Factory<Metrics>
    val eventListener: Single<EventListener>
    val dispatchers: Single<BukkitDispatchers>
    val scope: Single<AsyncComponent>
    val config: Reloadable<EmpireConfig>
    val translation: Reloadable<PluginTranslation>
    val eventManager: Factory<EventManager>
    val permissionManager: Single<PermissionManager>
    val translationContext: Provider<BukkitTranslationContext>
}
