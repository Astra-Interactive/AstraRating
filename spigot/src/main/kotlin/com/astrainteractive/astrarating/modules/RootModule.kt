package com.astrainteractive.astrarating.modules

import com.astrainteractive.astrarating.AstraRating
import com.astrainteractive.astrarating.domain.api.CachedApi
import com.astrainteractive.astrarating.domain.api.RatingDBApi
import com.astrainteractive.astrarating.domain.usecases.InsertUserUseCase
import com.astrainteractive.astrarating.events.EventManager
import com.astrainteractive.astrarating.integrations.papi.RatingPAPIComponent
import com.astrainteractive.astrarating.plugin.EmpireConfig
import com.astrainteractive.astrarating.plugin.PluginTranslation
import org.bstats.bukkit.Metrics
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.filemanager.SpigotFileManager
import ru.astrainteractive.astralibs.orm.Database
import ru.astrainteractive.klibs.kdi.Factory
import ru.astrainteractive.klibs.kdi.Lateinit
import ru.astrainteractive.klibs.kdi.Module
import ru.astrainteractive.klibs.kdi.Reloadable
import ru.astrainteractive.klibs.kdi.Single

interface RootModule : Module {
    val plugin: Lateinit<AstraRating>
    val bstats: Factory<Metrics>
    val eventListener: Single<EventListener>
    val dispatchers: Single<BukkitDispatchers>
    val scope: Single<AsyncComponent>
    val papiExpansion: Single<RatingPAPIComponent?>
    val configFileManager: Single<SpigotFileManager>
    val config: Reloadable<EmpireConfig>
    val translation: Reloadable<PluginTranslation>
    val database: Single<Database>
    val dbApi: Single<RatingDBApi>
    val cachedApi: Single<CachedApi>
    val eventManager: Factory<EventManager>
    val insertUserUseCase: Single<InsertUserUseCase>
}
